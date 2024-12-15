variable "key_name" {
  description = "Name of the EC2 key pair"
  type        = string
}

variable "db_password" {
  description = "The password for the PetClinic RDS database"
  type        = string
  sensitive   = true
}

variable "db_username" {
  description = "The username for the PetClinic RDS database"
  type        = string
  sensitive   = true
}

variable "my_ip" {
  description = "Public IP address for SSH and security group ingress"
  type        = string
}