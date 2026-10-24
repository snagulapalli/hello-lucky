# hello-lucky

An illustrative project. It follows one small Spring Boot service from source, through a container image, to a running task on Amazon ECS.

The service is a single REST endpoint. On each request it reads a phone country code from AWS Systems Manager Parameter Store and a lucky number from AWS Secrets Manager. The Terraform files in `terraform/` create that configuration, the network, and the ECS service, so the same path can be provisioned again later.

The long command-by-command walkthrough, including the manual AWS CLI version, is in [hello-lucky-deployment.html](hello-lucky-deployment.html) and [hello-lucky-deployment.pdf](hello-lucky-deployment.pdf).

## What the endpoint returns

`GET /hello-lucky/hello?country=US`

```json
{"country":"US","phone-country-code":1,"lucky-number":11}
```

`INDIA` returns phone country code `91` and lucky number `22`. An unknown country returns HTTP 400.

| Value | AWS service | Name |
| --- | --- | --- |
| Phone country code | Parameter Store | `/hello-lucky/phone-country-codes` |
| Lucky number | Secrets Manager | `hello-lucky/lucky-numbers` |

Both values are JSON maps, read on every request. The sample maps are `{"US":1,"INDIA":91}` and `{"US":11,"INDIA":22}`.

## End-to-end path

1. Build and test the Spring Boot app with Gradle.
2. Package the jar into a Docker image.
3. Push the image to Amazon ECR.
4. Provision the VPC, IAM roles, Parameter Store entry, secret, ECS cluster, task definition, and service with Terraform.
5. Call the endpoint on the task's public address.

## Build and test

Java 21. From this directory:

```bash
./gradlew test
./gradlew bootJar
```

`bootJar` writes `build/libs/hello-lucky-0.0.1-SNAPSHOT.jar`. To run it on your machine, the AWS profile and region must be set, and the parameter and secret must already exist:

```bash
AWS_PROFILE=personal AWS_REGION=us-east-1 ./gradlew bootRun
```

Then call `http://localhost:8080/hello-lucky/hello?country=US`.

## Docker image

The image is Amazon Corretto 21 on `linux/arm64`, which matches the ECS task:

```bash
docker build --platform linux/arm64 -t hello-lucky:latest .
```

After Terraform has created the ECR repository, tag and push `hello-lucky:latest`. In zsh, quote the tag as `${REPO}:latest`. The deployment guide has the login and push commands.

## Terraform

The AWS provider uses the profile `personal` and the region `us-east-1`.

| File | Creates |
| --- | --- |
| `versions.tf` | Provider settings |
| `network.tf` | VPC, subnets, route table, security group for TCP 8080 |
| `iam.tf` | Execution role and the task role that can read the parameter and the secret |
| `ecs.tf` | ECR repository, log group, cluster, task definition, and service |
| `config.tf` | The Parameter Store entry and the Secrets Manager secret |

Push the image before the service starts, because the task definition pulls `:latest`. `terraform apply` does not print the task's public IP. The deployment guide shows how to look it up and how to call the endpoint.

Fargate is billed while a task is running. When you are done:

```bash
terraform destroy
```

If Secrets Manager keeps the secret in a recovery window after destroy, delete it with `--force-delete-without-recovery`. That command is in the deployment guide.
