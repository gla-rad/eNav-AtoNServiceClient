# e-Navigation AtoN Service Client Service
This is a reference implementation for a SECOM-compliant AtoN Service Client
that can interface with the AtoN Service, create subscriptions and received
the incoming S-125 messages.

Note that this is just a reference implementation and it does not provide
a full functionality. For this reason, no database will be connected to this
service as well. All interactions are/will take place through the provided
web-interface.

## Development Setup
To start developing just open the repository with the IDE of your choice. The
original code has been generated using
[Intellij IDEA](https://www.jetbrains.com/idea). Just open it by going to:

    File -> New -> Project From Verson Control

Provide the URL of the current repository and the local directory you want.

You don't have to use it if you have another preference. Just make sure you
update the *.gitignore* file appropriately.

## Build Setup
The project is using the latest OpenJDK 17  to build, and only that should be
used. To build the project you will need Maven, which usually comes along-side
the IDE. Nothing exotic about the goals, just clean and install should do:

    mvn clean package

## Configuration
The configuration of the eureka server is based on the properties files found
in the *main/resources* directory.

The *boostrap.properties* contains the necessary properties to start the service
while the *application.properties* everything else i.e. the security
configuration.

Note that authentication is provided by Keycloak, so before you continue, you
need to make sure that a keycloak server is up and running, and that a client
is registered. Once that is done, the service will required the following
*application.properties* to be provided:

    keycloak.auth-server-url=<The keycloak address>
    keycloak.resource=<The client name>
    keycloak.credentials.secret=<The generated client sercet>

However, since a keycloak client does not yet exist for your template
service, for making things easy, the basic Spring security is enabled at
the beginning. To log in just use the credentials:
* Username: user
* Password: password

To remove the basic security and enable keycloak, just edit the 
*application.properties* file in the following sections:

    # Basic Security Configuration <== Remove this section
    spring.security.basic.enabled=true
    spring.security.user.name=user
    spring.security.user.password=password
    spring.security.user.role=USER,ADMIN
    
    # Keycloak Configuration
    keycloak.enabled=false #<== Enable this
    keycloak.auth-server-url=http://palatia.grad-rrnav.pub:8090/auth
    keycloak.realm=Niord
    keycloak.resource=c-keeper
    keycloak.credentials.secret=8b36b836-b4d7-4fc6-95d5-a402823f9a6b
    #keycloak.ssl-required=external
    keycloak.principal-attribute=preferred_username
    keycloak.autodetect-bearer-only=true
    keycloak.use-resource-role-mappings=true
    keycloak.token-minimum-time-to-live=30


## Running the Service
To run the service, just like any other Springboot micro-service, all you need
to do is run the main class, i.e. CKeeper. No further arguments are
required. Everything should be picked up through the properties files.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to
discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
Distributed under the Apache License. See [LICENSE](./LICENSE) for more
information.

## Contact
Nikolaos Vastardis - Nikolaos.Vastardis@gla-rad.org
