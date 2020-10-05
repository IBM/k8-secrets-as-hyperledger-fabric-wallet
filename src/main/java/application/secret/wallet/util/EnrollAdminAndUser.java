package application.secret.wallet.util;


import org.apache.commons.lang3.StringEscapeUtils;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Properties;
import java.util.Set;


public class EnrollAdminAndUser {



    static {

        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");

    }



    public static void main(String[] args) throws Exception {

        String connectionProfileName = args[0];
        String userAdmin = args[1];
        String userAdminPass = args[2];
        String userName = args[3];
        String userPass = args[4];

        ProfileVO profileData = ConnectionProfileLoader.loadProfileData(connectionProfileName);
        String caPem = profileData.getCaPem();
        String caUrl = profileData.getCaURL();
        String orgName = profileData.getConnectingOrgName();

        Properties props = new Properties();

        caPem = caPem.substring(1,(caPem.length()-1));
        caPem = StringEscapeUtils.unescapeJava(caPem);


        props.put("pemBytes", caPem.getBytes());

        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance(profileData.getCaName(),caUrl, props);

        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();

        caClient.setCryptoSuite(cryptoSuite);


        Wallet wallet = Wallet.createFileSystemWallet(Paths.get("wallet"));

        boolean userExists = wallet.exists(userPass);

        if (userExists) {

            System.out.println("An identity for the user already exists in the wallet");

            return;

        }

        userExists = wallet.exists(userAdmin);

        if (!userExists) {

            final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();

            enrollmentRequestTLS.addHost("localhost");

            enrollmentRequestTLS.setProfile("tls");

            Enrollment enrollment = caClient.enroll(userAdmin, userAdminPass, enrollmentRequestTLS);

            Identity user = Identity.createIdentity(orgName, enrollment.getCert(), enrollment.getKey());

            wallet.put(userAdmin, user);

            System.out.println("Successfully enrolled user \""+userAdmin+"\" and imported it into the wallet");

        }



        Identity adminIdentity = wallet.get(userAdmin);

        User admin = new User() {



            @Override

            public String getName() {

                return userAdmin;

            }



            @Override

            public Set<String> getRoles() {

                return null;

            }



            @Override

            public String getAccount() {

                return null;

            }



            @Override

            public String getAffiliation() {

                return "org1.department1";

            }



            @Override

            public Enrollment getEnrollment() {

                return new Enrollment() {



                    @Override

                    public PrivateKey getKey() {

                        return adminIdentity.getPrivateKey();

                    }



                    @Override

                    public String getCert() {

                        return adminIdentity.getCertificate();

                    }

                };

            }



            @Override

            public String getMspId() {

                return orgName;

            }



        };




        RegistrationRequest registrationRequest = new RegistrationRequest(userName);

        registrationRequest.setAffiliation("org1.department1");

        registrationRequest.setEnrollmentID(userName);

        registrationRequest.setSecret(userPass);

        caClient.register(registrationRequest, admin);

        Enrollment enrollment = caClient.enroll(userName, userPass);

        Identity user = Identity.createIdentity(orgName, enrollment.getCert(), enrollment.getKey());


        System.out.println("MSP(base 64):"+Base64.getEncoder().encodeToString(orgName.getBytes()));

        System.out.println("Public cert(base 64):"+Base64.getEncoder().encodeToString(enrollment.getCert().getBytes()));


        wallet.put(userName, user);
        File fPrivateCert = new File("./wallet/"+userName+"/"+userName+"-priv");

        String key = new String(Files.readAllBytes(fPrivateCert.toPath()));
        key = key.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");

        String encoded = Base64.getEncoder().encodeToString(key.getBytes());

        System.out.println("Private key(base 64):"+encoded);


    }



}