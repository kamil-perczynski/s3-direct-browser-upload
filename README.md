# S3 Direct Browser Upload

Project was created due to lack of `createPresignedPost` operation in AWS S3 Java SDK. 
Its intention is to mimic that behavior while still being compliant with the AWS documentation .

## Running

The easiest way to run the project is to open it in Intellij and run the `main` method in the class `UploadsApplication`.

The application expects that AWS credentials are already configured, and the user defined environment variable
`BUCKET_NAME` that contains the bucket name to which uploads will be sent.

## Endpoints

The application exposes HTTP server on port `5000` and two endpoints:

| Endpoint                     | Description                                                                        |
|------------------------------|------------------------------------------------------------------------------------|
| http://localhost:5000/       | It renders the form, you may use to test direct upload from the browser            |
| http://localhost:5000/s3/url | Exposes output from the implemented `createPresignedPost` operation in JSON format |
