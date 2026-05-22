# SMD Assignment 3: Keyboard Spyware
#### Team

Maria Sfîrăială, SAS1  
Andrei Petrea, SAS1

## Architectural Design

![architecture](https://i.imgur.com/1sjOaRC.png)

## Flow Diagram

![diagram2](https://i.imgur.com/JVdGFnA.png)

## Planning

### Infrastructure

We will host the source code for both the Android application and remote server on GitHub to ensure proper development best practices like code review and bug fixing. We will also use it for CI/CD's pipeline to package the app as an APK bundled with the *root CA certificate* used for authenticating the remote server, as well as building the container image for the server alongside it's signed certificates.

### Android Application

The application is bootstrapped via *Gradle* and will contain the following components:

- **Theme customization activity** - launching the app will open this where users can choose between different themes for the keyboard as well as enabling and setting it as the default
- **Keyboard service** - this will override the default keyboard service when using other applications and send the messages to the remote party
- **HTTPS Client** - the client component which will handle initiating and maintaining the connection between the app and server
- **Payload DTO** - the payload class that contains the message typed + a unique identifier for the user sending the message

In order to pass the app as legitimate, we will also use custom assets such as AppIcons to create the image of a trusted application.

### Remote server

The remote server consists of a *VM* running a *Docker Compose* stack comprising of the following containers:

1. **Go HTTPS server** - this handles the requests send by the clients and writes the messages into a persistent volume storage for further processing. 
2. **Fluentbit** - this parses the logs into a usable format and sends it farther along the observability pipeline
3. **Loki** - log aggregator that groups the messages into buckets by their client origin.
4. **Grafana** - data visualizer for the Loki buckets
