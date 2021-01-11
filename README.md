# TEMP-CHAT

Android application to create temporale chat with people and socketserver using node js

## About

Create chat rooms, chat with people without register needed

## Project status

Project has done

## Setup

### Requirements

This project was developed and tested under the following:

- Android min versión: 8.0 (API level 26)
- Nodejs (v12.19.0)

### Get Started

1. Install dependencies: `npm ci`
2. Run server `npm start`
3. Modify `DEFAULT_SERVER_ADDRESS` in [GlobalConfig.java], use 10.0.0.2 for local AVD.
4. Import project into Android Studio
5. Run project

### Package.json Scripts

- **start**: Runs socketserver
- **dev**: Runs socketserver on dev mode (using nodemoon)

[globalconfig.java]: https://github.com/ITL-Teams/temp-chat/blob/master/app/src/main/java/com/example/tempchat/utils/GlobalConfig.java
