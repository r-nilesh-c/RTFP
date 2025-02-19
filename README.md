# Secure File Transfer System

A secure, encrypted file transfer system built with Spring Boot that allows users to securely share files through temporary links and one-time codes. Files are automatically encrypted during upload and decrypted during download, with automatic cleanup of expired files.

## Features

- Secure file encryption using custom byte cipher algorithm
- Automatic file expiration after 10 minutes
- One-time use access codes for file retrieval
- Automatic cleanup of expired files
- Support for large file transfers (up to 100MB)
- Cross-platform compatibility
- Web-based interface for easy file sharing
- Public access capability through ngrok integration
- Real-time file transfer status
- Secure temporary storage with automatic deletion
- CORS support for cross-origin requests
- Detailed logging and error handling
- Clean shutdown with automatic cleanup

## Prerequisites

- Java JDK 11 or higher
- Maven 3.6+ for building the project
- Minimum 1GB free disk space for file storage
- Modern web browser (Chrome, Firefox, Safari, or Edge)
- Port 8080 available on the host machine
- Write permissions in the home directory for file storage
- ngrok (optional, for public access)
- Operating System: Windows, macOS, or Linux
## Installation & Setup

1. Clone the repository: git clone https://github.com/r-nilesh-c/secure-file-transfer.git 
                         cd secure-file-transfer
2. Configure the application:
   - Open `/src/main/resources/application.properties`
   - Modify the following properties as needed:
   - server.port=8080 
   - file.upload.dir=${user.home}/file-transfer-system 
   - spring.servlet.multipart.max-file-size=100MB 
   - spring.servlet.multipart.max-request-size=100MB
3. Build the project: mvn clean install
4. Run the application:
   - java -jar target/secure-file-transfer-1.0-SNAPSHOT.jar

## Using ngrok for Public Access

1. Install ngrok:
   - Download from https://ngrok.com/download
   - Follow installation instructions for your operating system

2. Start ngrok tunnel:
   - ngrok http 8080

3. Configure the application:
   - Copy the ngrok URL (e.g., https://xxxx-xx-xx-xxx-xx.ngrok.io)
   - Use this URL when accessing the application
   - Update the server address in the web interface

## Important Configuration Changes

Before running the application, make sure to:

1. Set up the upload directory:
    - The default upload directory is `${user.home}/file-transfer-system`
    - Ensure the directory exists and has proper read/write permissions
    - You can change this in `application.properties`

2. Configure server address:
    - The default server runs on `localhost:8080`
    - For network access, configure your firewall and network settings
    - Update the server address in the web interface when accessing

3. Security considerations:
    - The encryption keys are stored in the `encryption.keys` file
    - Ensure this file has proper permissions
    - Consider implementing additional security measures for production use

## Usage

1. Starting the server:
    - java -jar target/secure-file-transfer-1.0-SNAPSHOT.jar
2. Accessing the web interface:
    - Open a web browser
    - Navigate to `http://localhost:8080` (or your configured address)
    - Enter the server address in the interface

3. Sending files:
    - Click "Choose File" to select a file
    - Click "Send File" to upload
    - Copy the generated file ID

4. Receiving files:
    - Enter the file ID in the "Receive File" section
    - Click "Download File"
    - Files expire after 10 minutes

## Security Features


## Troubleshooting

1. Upload fails:
    - Check file size limits in `application.properties`
    - Verify upload directory permissions
    - Ensure enough disk space

2. Download fails:
    - Verify file hasn't expired (10-minute limit)
    - Check if file ID is correct
    - Ensure server is running

3. Server won't start:
    - Check port availability
    - Verify Java version
    - Check directory permissions

4. ngrok issues:
    - Verify ngrok is running correctly
    - Check ngrok tunnel status
    - Ensure correct port forwarding (8080)

## Development Notes


## Project Structure
secure-file-transfer/
├── src/ 
│ ├── main/ 
│ │ ├── java/ 
│ │ │ └── com/yourproject/ 
│ │ │ ├── controller/ 
│ │ │ ├── model/ 
│ │ │ ├── service/ 
│ │ │ └── utils/ 
│ │ └── resources/ 
│ │ ├── templates/ 
│ │ └── application.properties 
├── pom.xml 
└── README.md

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Disclaimer

This project is for educational purposes. While it implements basic security measures, additional security considerations should be implemented for production use. Note: When using ngrok, be aware of its security implications and limitations in the free tier.

## Support

For issues and feature requests, please create an issue in the GitHub repository.
