## *** Work in progress ***

## K8 secrets as Hyperledger Fabric wallet using Fabric Java SDK

Security on the Hyperledger Fabric is enforced with digital signatures. All requests made to the fabric must be signed by users with appropriate enrolment certificates. Once user is enrolled, application persist certificate in wallet for future usages.

There are existing Fabric wallets like FileSystemWallet, CouchDBWallet which developers can leverage to store Blockchain identities. The security concern with these implementations is that they “externalize” associated privateKey of the identity which can be compromised if someone get access to these storage systems which are outside of Kubernetes platform where Fabric SDK is deployed.

In most of the scenarios, front-end client application and client SDK application (integration layer) gets deployed to Kubernetes cluster. Hence we propose the solution to store certificate wallet in Kubernetes platform itself. It can be considered more secure since it removes dependency to store wallet outside of Kubernetes cluster. Also, there is no additional cost associated to use Kubernetes secretes and existing Kubernetes cluster can be used. This code pattern demonstrates how to store certificates as K8s secrets and the use of secrets wallet further using Fabric JAVA SDK.

## Flow


## Pre-requisites

* [IBM Cloud Account](https://cloud.ibm.com)
* [Git Client](https://git-scm.com/downloads) - needed for clone commands.
* [JDK 11](http://jdk.java.net/archive/)
* [Maven]()

## Steps

Follow these steps to setup and run this code pattern. The steps are described in detail below.
1. [Get the code](#1-get-the-code)
2. [Create IBM Cloud Services](#2-create-ibm-cloud-services)
3. [Setup Hyperledger Fabric Network using IBM Blockchain Platform](#3-setup-hyperledger-fabric-network-using-ibm-blockchain-platform)

## 1. Get the code

- Clone the repo using the below command.
   ```
   git clone 
   ```
   
## 2. Create IBM Cloud Services

**Create IBM Kubernetes Service Instance**

Create a Kubernetes cluster with [Kubernetes Service](https://cloud.ibm.com/kubernetes/catalog/create) using IBM Cloud Dashboard.

  > Note: It can take up to 15-20 minutes for the cluster to be set up and provisioned.  

**Create IBM Blockchain Platform Service Instance**

Create [IBM Blockchain Platform Service](https://cloud.ibm.com/catalog/services/blockchain-platform) instance using IBM Cloud Dashboard.

## 3. Setup Hyperledger Fabric Network using IBM Blockchain Platform

Follow this [tutorial](https://developer.ibm.com/tutorials/quick-start-guide-for-ibm-blockchain-platform/) to create fabric network using IBM Blockchain Platform. You can decide network components (number of organizations, number of peers in each org etc.) as per your requirement. For example, the blockchain network may consist of two organizations with single peer each and an orderer service for carrying out all the transactions.

**Chaincode Install & Instantiation and Download Connection Profile**

This code pattern can be executed with the sample chaincode [fabcar.go](https://github.com/hyperledger/fabric-samples/tree/release-1.4/chaincode/fabcar/go) or else you can install your own chaincode. Instantiate the chaincode after installation.

You can refer to step 12 to step 15 [here](https://developer.ibm.com/tutorials/quick-start-guide-for-ibm-blockchain-platform/) to install smart contract, instantiate and then download connection profile. The downloaded connection profile will be used in further steps.

## Register and enroll user to connect to Hyperledger Fabric Network

Go to the cloned repository code.
Copy the downloaded connection profile(in previous step) at `src/main/resources`
Update the value of `connection-profile` key by the name of your downloaded connection profile in `src/main/resources/application.yml`.
Run the following command in your terminal window.
utility class "application.secret.wallet.util.EnrollAdminAndUser" with below arguments to Register and enrol Blockchain User as below
```
   mvn clean install
   mvn exec:java -Dexec.args="org1msp_profile.json admin adminpw shikha shikhapw
```

## Deploy the Fabric Java SDK Client application on IBM Kubernetes Service

As discussed before, need to decide on which Kubernetes cluster you would like to deploy the application. The application can be deployed on Kubernetes using devops toolchain.

* Create a [toolchain](https://cloud.ibm.com/devops/create) to `Develop a Kubernetes App`.
* Follow the instructions to deploy your application explained [here](https://www.ibm.com/cloud/architecture/tutorials/use-develop-kubernetes-app-toolchain?task=1).

> Note: You may need to fork the repository and provide that Github URL to toolchain.










## Learn More


## License

This code pattern is licensed under the Apache Software License, Version 2. Separate third-party code objects invoked within this code pattern are licensed by their respective providers pursuant to their own separate licenses. Contributions are subject to the [Developer Certificate of Origin, Version 1.1 (DCO)](https://developercertificate.org/) and the [Apache Software License, Version 2](https://www.apache.org/licenses/LICENSE-2.0.txt).

[Apache Software License (ASL) FAQ](https://www.apache.org/foundation/license-faq.html#WhatDoesItMEAN)
