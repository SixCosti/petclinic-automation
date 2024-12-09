provider "aws" {
  region = "eu-west-1"
}

resource "aws_instance" "pet_clinic" {
  ami           = "ami-0715d656023fe21b4" # Debian AMI
  instance_type = "t2.micro"
  key_name      = var.key_name
  tags = {
    Name = "PetClinicServer"
  }

  user_data = <<-EOF
    #!/bin/bash
    sudo apt update -y
    sudo apt install -y docker.io docker-compose python3
    sudo systemctl enable docker
    sudo systemctl start docker
  EOF

  vpc_security_group_ids = [aws_security_group.petclinic_sg.id]

  # Copy docker-compose.yml to the instance
  provisioner "file" {
    source      = "../docker-compose.yml"
    destination = "/home/admin/docker-compose.yml"
    connection {
      type        = "ssh"
      user        = "admin"
      private_key = file("~/.ssh/${var.key_name}.pem")
      host        = self.public_ip
    }
  }

  # Generate inventory file for Ansible
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

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
