output "public_ip" {
  value = aws_instance.pet_clinic.public_ip
}

output "rds_endpoint" {
  value       = aws_db_instance.petclinic_db.endpoint
  description = "RDS endpoint for the PetClinic database"
}
