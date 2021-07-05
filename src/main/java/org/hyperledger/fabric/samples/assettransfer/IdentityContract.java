/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.text.ParseException;
import java.util.*;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "identity",
        info = @Info(
                title = "Identity Manager Contect",
                description = "The LSDi Identity Management Contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "andre.cardoso@lsdi.ufma.br",
                        name = "André Cardoso",
                        url = "http://www.lsdi.ufma.br/")))
@Default
public final class IdentityContract implements ContractInterface {

    private final Genson genson = new Genson();

    private enum IdentityErrors {
        IDENTITY_NOT_FOUND,
        IDENTITY_ALREADY_EXISTS,
        SIGNATURE_INVALID,
        SIGNATURE_PAYLOAD_DOES_NOT_MATCH
    }

    /**
     * Create one initial identity on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();



        CreateIdentity(ctx, "http://www.lsdi.ufma.br/" ,"lsdi:identity:first", "lsdi:identity:first");


    }



    /**
     * Creates a new identity on the ledger.
     *
     * @param ctx the transaction context
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Identity CreateIdentity(final Context ctx,
                                   final String context,
                                   final String id,
                                   final String controlledBy) {
        ChaincodeStub stub = ctx.getStub();

        if (IdentityExists(ctx, id)) {
            String errorMessage = String.format("Identity %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, IdentityErrors.IDENTITY_ALREADY_EXISTS.toString());
        }

        Identity identity = new Identity(context, id, controlledBy, null , null ,"active", null , null);

        String assetJSON = genson.serialize(identity);
        stub.putStringState(id, assetJSON);
        return identity;
    }

    /**
     * Creates a new identity on the ledger. This method expects only ECKeys as key;
     *
     * @param ctx the transaction context
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Identity CreateECIdentity(
            final Context ctx) throws JOSEException {

        ChaincodeStub stub = ctx.getStub();
        List<String> args  = stub.getParameters();

        String context      = args.get(0);
        String identifier   = args.get(1);
        String controlledBy = args.get(2);
        String kty          = args.get(3);
        String kid          = args.get(4);
        String alg          = args.get(5);
        String crv          = args.get(6);
        String x            = args.get(7);
        String y            = args.get(8);
        String signature    = args.get(9);


        HashMap<String, String> publicKeyJwk = new HashMap<>();
        publicKeyJwk.put("kty", kty);
        publicKeyJwk.put("kid", kid);
        publicKeyJwk.put("crv", crv);
        publicKeyJwk.put("alg", alg);
        publicKeyJwk.put("x",   x);
        publicKeyJwk.put("y",   y);


        String[] dates = Utils.getIssueAndExpiracyDate(1);


        HashMap<String, String> subjectInfo  = new HashMap<>();
        for (int i = 10; i < args.size(); i++) {
            String[] split = args.get(i).split(":");
            subjectInfo.put(split[0], split[1]);
        }


        if (IdentityExists(ctx, identifier)) {
            String errorMessage = String.format("Identity %s already exists", identifier);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, IdentityErrors.IDENTITY_ALREADY_EXISTS.toString());
        }
        if(!identifier.equals(controlledBy)){
            if (!IdentityExists(ctx, controlledBy)) {
                String errorMessage = String.format("Parent Identity %s does not exists", identifier);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, IdentityErrors.IDENTITY_NOT_FOUND.toString());
            }
        }



        JWSObject objReceiver = null;
        boolean verified = false;
        boolean match = false;

        try {
            objReceiver = JWSObject.parse(signature);
            JSONObject jsonKey = new JSONObject(publicKeyJwk);
            ECKey ecPublicJWK = JWK.parse(jsonKey).toECKey().toPublicJWK();
            JWSVerifier verifier = new ECDSAVerifier(ecPublicJWK);
            verified = objReceiver.verify(verifier);
            match = identifier.equals(objReceiver.getPayload().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(!verified ){
            System.out.println("Signature invalid");
            throw new ChaincodeException("Signature invalid", IdentityErrors.IDENTITY_NOT_FOUND.toString());
        }
        if(!match){
            System.out.println("Signature payload does not match with identifier");
            throw new ChaincodeException("Signature invalid", IdentityErrors.SIGNATURE_PAYLOAD_DOES_NOT_MATCH.toString());
        }

        Identity identity = new Identity(context, identifier, controlledBy, publicKeyJwk, subjectInfo, "active", dates[0], dates[1]);
        String assetJSON = genson.serialize(identity);
        stub.putStringState(identifier, assetJSON);
        return identity;
    }







    /**
     * Retrieves an asset with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param id the ID of the identity
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Identity ReadIdentity(final Context ctx, final String id) {
        ChaincodeStub stub = ctx.getStub();
        String identityJSON = stub.getStringState(id);

        if (identityJSON == null || identityJSON.isEmpty()) {
            String errorMessage = String.format("Identity %s does not exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, IdentityErrors.IDENTITY_NOT_FOUND.toString());
        }

        Identity identity = genson.deserialize(identityJSON, Identity.class);
        return identity;
    }

    /**
     * Checks the existence of the identity on the ledger
     *
     * @param ctx the transaction context
     * @param id the ID of the identity
     * @return boolean indicating the existence of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean IdentityExists(final Context ctx, final String id) {
        ChaincodeStub stub = ctx.getStub();
        String identityJSON = stub.getStringState(id);

        return (identityJSON != null && !identityJSON.isEmpty());
    }



    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return array of assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllIdentities(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Identity> queryResults = new ArrayList<Identity>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Identity identity = genson.deserialize(result.getStringValue(), Identity.class);
            queryResults.add(identity);
            System.out.println(identity.toString());
        }

        final String response = genson.serialize(queryResults);

        return response;
    }
}
