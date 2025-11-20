output "deployment_time" {
  description = "When infrastructure was deployed"
  value       = timestamp()
}

output "files_created" {
  description = "Files created by Terraform"
  value = [
    local_file.jenkins_test.filename,
    local_file.app_config.filename
  ]
}