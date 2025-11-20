terraform {
  required_version = ">= 1.0.0" # Ensure that the Terraform version is 1.0.0 or higher
}

provider "aws" {
  region = "us-east-1" # Set the AWS region to US East (N. Virginia)
}

resource "local_file" "jenkins_test" {
  content  = "Infrastructure provisioned by Jenkins on ${timestamp()}"
  filename = "${path.module}/jenkins-deployed.txt"
}
resource "local_file" "app_config" {
  content = jsonencode({
    app_name    = "ItextPDF Application"
    environment = "development"
    version     = "1.0"
    deployed_by = "Jenkins"
  })
  filename = "${path.module}/config.json"
}