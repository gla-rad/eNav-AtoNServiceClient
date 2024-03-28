# e-Navigation AtoN Service Client

This is a reference implementation for a SECOM-compliant AtoN Service Client
that can interface with the AtoN Service, create subscriptions and received
the incoming S-125 messages. The client will focus on interoperability with
the current GRAD e-Navigation
[AtoN Service](https://github.com/gla-rad/eNav-AtoNService), which is also
available on the  [GRAD Github account](https://github.com/gla-rad/) .

Note that this is just a reference implementation and it does not provide
a full functionality. For this reason, no database will be connected to this
service. All interactions are/will take place through the provided web-interface.

## What is e-Navigation

The maritime domain is facing a number for challenges, mainly due to the
increasing demand, that may increase the risk of an accident or loss of life.
These challenges require technological solutions and e-Navigation is one such
solution. The International Maritime Organization ([IMO](https://www.imo.org/))
adopted a ‘Strategy for the development and implementation of e‐Navigation’
(MSC85/26, Annexes 20 and 21), providing the following definition of
e‐Navigation:

<div style="padding: 4px;
    background:lightgreen;
    border:2px;
    border-style:solid;
    border-radius:20px;
    color:black">
E-Navigation, as defined by the IMO, is the harmonised collection, integration,
exchange, presentation and analysis of maritime information on-board and ashore
by electronic means to enhance berth-to-berth navigation and related services,
for safety and security at sea and protection of the marine environment.
</div>

In response, the International Association of Lighthouse Authorities
([IALA](https://www.iala-aism.org/)) published a number of guidelines such as
[G1113](https://www.iala-aism.org/product/g1113/) and
[G1114](https://www.iala-aism.org/product/g1114/), which establish the relevant
principles for the design and implementation of harmonised shore-based technical
system architectures and propose a set of best practices to be followed. In
these, the terms Common Shore‐Based System (CSS) and Common Shore‐based System
Architecture (CSSA) were introduced to describe the shore‐based technical system
of the IMO’s overarching architecture.

To ensure the secure communication between ship and CSSA, the International
Electrotechnical Commission (IEC), in coordination with IALA, compiled a set of
system architecture and operational requirements for e-Navigation into a
standard better known as [SECOM](https://webstore.iec.ch/publication/64543).
This provides mechanisms for secure data exchange, as well as a TS interface
design that is in accordance with the service guidelines and templates defined
by IALA. Although SECOM is just a conceptual standard, the Maritime Connectivity
Platform ([MCP](https://maritimeconnectivity.net/)) provides an actual
implementation of a decentralised framework that supports SECOM.

## What is the GRAD e-Navigation Service Architecture

The GLA follow the developments on e-Navigation closely, contributing through
their role as an IALA member whenever possible. As part of their efforts, a
prototype GLA e-Navigation Service Architecture is being developed by the GLA
Research and Development Directorate (GRAD), to be used as the basis for the
provision of the future GLA e-Navigation services.

As a concept, the CSSA is based on the Service Oriented Architecture (SOA). A
pure-SOA approach however was found to be a bit cumbersome for the GLA
operations, as it usually requires the entire IT landscape being compatible,
resulting in high investment costs. In the context of e-Navigation, this could
become a serious problem, since different components of the system are designed
by independent teams/manufacturers. Instead, a more flexible microservice
architecture was opted for. This is based on a break-down of the larger
functional blocks into small independent services, each responsible for
performing its own orchestration, maintaining its own data and communicating
through lightweight mechanisms such as HTTP/HTTPS. It should be pointed out that
SOA and the microservice architecture are not necessarily that different.
Sometimes, microservices are even considered as an extension or a more
fine-grained version of SOA.

## The e-Navigation AtoN Service Client

This is a demonstration client that can be used to test the SECOM operations
of the original e-Navigation
[AtoN Service](https://github.com/gla-rad/eNav-AtoNService) developed by
GRAD. More specifically, it is able to perform a subscription to the
AtoN Service, using the mechanism specified by SECOM. The first step in this
process is to lookup the SECOM endpoint URL of the AtoN Service in a
SECOM-compliant service registry. Up to this point, only communication with the
[MCP Service Registry](https://github.com/maritimeconnectivity/ServiceRegistry)
has been tested. Once the required AtoN Service endpoint is discovered, the AtoN
Service Client will perform the subscription request using the X.509 certificate
provided in the configuration. Upon successful request, it will start receiving
the updated AtoN information in S-125 format. The data will then be presented
onto the web-interface of the client.

Note that only a simple marker with the display name will be shown.

## Development Setup

To start developing just open the repository with the IDE of your choice. The
original code has been generated using
[Intellij IDEA](https://www.jetbrains.com/idea). Just open it by going to:

    File -> New -> Project From Verson Control

Provide the URL of the current repository and the local directory you want.

You don't have to use it if you have another preference. Just make sure you
update the *.gitignore* file appropriately.

## Build Setup
The project is using the latest OpenJDK 21 to build, although earlier versions
should also work.

To build the project you will need Maven, which usually comes along-side the
IDE. Nothing exotic about the goals, just clean and install should do:

    mvn clean package

## How to Run

This service can be used in two ways (based on the use or not of the Spring Cloud
Config server).
* Enabling the cloud config client and using the configurations located in an
  online repository.
* Disabling the cloud config client and using the configuration provided
  locally.

### Cloud Config Configuration

In order to run the service in a **Cloud Config** configuration, you just need
to provide the environment variables that allow is to connect to the cloud
config server. This is assumed to be provided the GRAD e-Navigation Service
Architecture [Eureka Service](https://github.com/gla-rad/eNav-Eureka).

The available environment variables are:

    ENAV_CLOUD_CONFIG_URI=<The URL of the eureka cloud configuration server>
    ENAV_CLOUD_CONFIG_BRANCH=<The cloud configuration repository branch to be used>
    ENAV_CLOUD_CONFIG_USERNAME=<The cloud configration server username>
    ENAV_CLOUD_CONFIG_PASSWORD=<The cloud configration server password>

The parameters will be picked up and used to populate the default
**bootstrap.properties** of the service that look as follows:

    server.port=8768
    spring.application.name=aton-service-client
    spring.application.version=<application.version>
    
    # The Spring Cloud Discovery Config
    spring.cloud.config.uri=${ENAV_CLOUD_CONFIG_URI}
    spring.cloud.config.username=${ENAV_CLOUD_CONFIG_USERNAME}
    spring.cloud.config.password=${ENAV_CLOUD_CONFIG_PASSWORD}
    spring.cloud.config.label=${ENAV_CLOUD_CONFIG_BRANCH}
    spring.cloud.config.fail-fast=false

As you can see, the service is called **aton-service** and uses the **8766**
port when running.

To run the service, along with the aforementioned environment variables, you can
use the following command:

    java -jar \
        -DENAV_CLOUD_CONFIG_URI='<cloud config server url>' \
        -DENAV_CLOUD_CONFIG_BRANCH='<cloud config config repository branch>' \
        -DENAV_CLOUD_CONFIG_USERNAME='<config config repository username>' \
        -DENAV_CLOUD_CONFIG_PASSWORD='<config config repository passord>' \
        <aton-service-client.jar>

### Local Config Configuration

In order to run the service in a **Local Config** configuration, you just need
to provide a local configuration directory that contains the necessary
**.properties** files (including bootstrap).

This can be done in the following way:

    java -jar \
        --spring.config.location=optional:classpath:/,optional:file:<config_dir>/ \
        <aton-service-client.jar>

Examples of the required properties files can be seen below.

For bootstrapping, we need to disable the cloud config client, and clear our the
environment variable inputs:

    server.port=8768
    spring.application.name=aton-service-client
    spring.application.version=<application.version>
    
    # Disable the cloud config
    spring.cloud.config.enabled=false
    
    # Clear out the environment variables
    spring.cloud.config.uri=
    spring.cloud.config.username=
    spring.cloud.config.password=
    spring.cloud.config.label=

While the application properties need to provide the service with an OAuth2.0
server like keycloak, logging configuration, the eureka client connection etc.:

    # Configuration Variables
    service.variable.eureka.server.name=<eureka.server.name>
    service.variable.eureka.server.port=<eureka.server.port>
    service.variable.keycloak.server.name=<keycloak.server.name>
    service.variable.keycloak.server.port=<keycloak.server.port>
    service.variable.keycloak.server.realm=<keycloak.realm>
    service.variable.mcp.service-registry.endpoint=<msr-url>
    service.variable.mcp.aton-service.mrn=<subscription-service-mrn>
    
    # Eureka Client Configuration
    eureka.client.service-url.defaultZone=http://${service.variable.eureka.server.name}:${service.variable.eureka.server.port}/eureka/
    eureka.client.registryFetchIntervalSeconds=5
    eureka.instance.preferIpAddress=true
    eureka.instance.leaseRenewalIntervalInSeconds=10
    eureka.instance.metadata-map.startup=${random.int}
    
    # Spring-boot Admin Configuration
    spring.boot.admin.client.url=http://${service.variable.server.eureka.name}:${service.variable.server.eureka.port}/admin
    
    # Logging Configuration
    logging.file.name=/var/log/${spring.application.name}.log
    logging.file.max-size=10MB
    logging.pattern.rolling-file-name=${spring.application.name}-%d{yyyy-MM-dd}.%i.log
    
    # Management Endpoints
    management.endpoint.logfile.external-file=/var/log/${spring.application.name}.log
    management.endpoints.web.exposure.include=*
    management.endpoint.health.show-details=when_authorized
    management.endpoint.health.probes.enabled=true
    management.endpoint.httpexchanges.enabled=true
    
    # Springdoc configuration
    springdoc.swagger-ui.path=/swagger-ui.html
    springdoc.swagger-ui.display-query-params=true
    springdoc.swagger-ui.url=/api/secom/openapi.json
    #springdoc.swagger-ui.url=/v3/api-docs
    springdoc.packagesToScan=org.grad.eNav.atonServiceClient.controllers.secom
    
    ## Keycloak Configuration
    spring.security.oauth2.client.registration.keycloak.client-id=raiman
    spring.security.oauth2.client.registration.keycloak.client-secret=<changeit>
    spring.security.oauth2.client.registration.keycloak.client-name=Keycloak
    spring.security.oauth2.client.registration.keycloak.provider=keycloak
    spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
    spring.security.oauth2.client.registration.keycloak.scope=openid
    spring.security.oauth2.client.registration.keycloak.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
    spring.security.oauth2.client.provider.keycloak.issuer-uri=https://${service.variable.keycloak.server.name}:${service.variable.keycloak.server.port}/realms/${service.variable.keycloak.server.realm}
    spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
    spring.security.oauth2.resourceserver.jwt.issuer-uri=https://${service.variable.keycloak.server.name}:${service.variable.keycloak.server.port}/realms/${service.variable.keycloak.server.realm}
    
    # SECOM configuration
    secom.service-registry.url=${service.variable.mcp.service-registry.endpoint}
    secom.security.ssl.keystore=keystore.jks
    secom.security.ssl.keystore-password=<changeit>
    secom.security.ssl.keystore-type=JKS
    secom.security.ssl.truststore=truststore.jks
    secom.security.ssl.truststore-password=<changeit>
    secom.security.ssl.truststore-type=JKS
    secom.security.ssl.insecureSslPolicy=true
    gla.rad.aton-service-client.secom.serviceMrn=${service.variable.mcp.aton-service.mrn}
    gla.rad.aton-service-client.secom.certificateAlias=aton-service-client
    gla.rad.aton-service-client.secom.rootCertificateAlias=mcp-root
    gla.rad.aton-service-client.secom.signing-algorithm=SHA3-384withECDSA
    
    # Front-end Information
    gla.rad.aton-service-client.info.name=AtoN Service Client
    gla.rad.aton-service-client.info.version=${spring.application.version}
    gla.rad.aton-service-client.info.operatorName=Research and Development Directorate of GLA of UK and Ireland
    gla.rad.aton-service-client.info.operatorContact=Nikolaos.Vastardis@gla-rad.org
    gla.rad.aton-service-client.info.operatorUrl=https://www.gla-rad.org/
    gla.rad.aton-service-client.info.copyright=\u00A9 2023 GLA Research & Development
    gla.rad.aton-service-client.info.mrn=urn:mrn:mcp:service:gla:rad:org:aton-service-client:005
    gla.rad.aton-service-client.data-product.location=https://rnavlab.gla-rad.org/aton-service/xsd/S125.xsd

## Contributing
Pull requests are welcome. For major changes, please open an issue first to
discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
Distributed under the Apache License. See [LICENSE](./LICENSE.md) for more
information.

## Contact
Nikolaos Vastardis - Nikolaos.Vastardis@gla-rad.org
