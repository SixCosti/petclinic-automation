provider "aws" {
  region = "eu-west-1"
}

resource "aws_instance" "pet_clinic" {
  ami           = "ami-0715d656023fe21b4"
  instance_type = "t2.medium"
  key_name      = var.key_name
  tags = {
    Name = "PetClinicServer"
  }

  root_block_device {
    volume_size           = 20
    volume_type           = "gp3"
    delete_on_termination = true
  }

  user_data = <<-EOF
    #!/bin/bash
    sudo apt update -y
  EOF

  vpc_security_group_ids = [aws_security_group.petclinic_sg.id]

  provisioner "file" {
    source      = "kubernetes/deployment.yaml"
    destination = "/home/admin/deployment.yaml"
    connection {
      type        = "ssh"
      user        = "admin"
      private_key = file("~/.ssh/${var.key_name}.pem")
      host        = self.public_ip
    }
  }

  provisioner "local-exec" {
    command = <<EOT
    echo "[all]" > ansible/inventory.ini
    echo "${self.public_ip} ansible_ssh_user=admin ansible_ssh_private_key_file=~/.ssh/${var.key_name}.pem" >> ansible/inventory.ini
    EOT
  }
}

resource "aws_security_group" "petclinic_sg" {
  name = "PetClinicSG"

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 9966
    to_port     = 9966
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 0
    to_port     = 65535  # All TCP ports
    protocol    = "tcp"
    cidr_blocks = ["my_ip"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
