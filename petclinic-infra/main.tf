terraform {
  backend "s3" {
    bucket         = "terraform-state-bucket-00"
    key            = "terraform-tfstate/terraform.tfstate"
    region         = "eu-west-1"
    encrypt        = true
  }
}

provider "aws" {
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  region = "eu-west-1"
}

resource "null_resource" "inventory_update" {
  triggers = {
    always_run = "${timestamp()}"
  }

  provisioner "local-exec" {
    command = <<-EOT
      echo "[app]" > ansible/inventory.ini
      echo "${aws_instance.pet_clinic.public_ip} ansible_ssh_user=admin ansible_ssh_private_key_file=~/.ssh/${var.key_name}.pem" >> ansible/inventory.ini
      echo "[db]" >> ansible/inventory.ini
      echo "${aws_db_instance.petclinic_db.address}" >> ansible/inventory.ini
    EOT

  }
  depends_on = [aws_db_instance.petclinic_db]
}



resource "aws_instance" "pet_clinic" {
  ami           = "ami-0715d656023fe21b4"
  instance_type = "t2.medium"
  key_name      = var.key_name
  monitoring    = false # Ensures basic monitoring is enabled

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
    echo "DB_HOST=${aws_db_instance.petclinic_db.address}" | sudo tee -a /etc/environment
    source /etc/environment
  EOF

  vpc_security_group_ids = [aws_security_group.petclinic_sg.id]

  depends_on = [aws_db_instance.petclinic_db]


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
}

resource "aws_cloudwatch_metric_alarm" "ec2_cpu_high" {
  alarm_name          = "HighCPUUtilization"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = 1
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  dimensions = {
    InstanceId = aws_instance.pet_clinic.id
  }
}

resource "aws_cloudwatch_metric_alarm" "rds_free_storage" {
  alarm_name          = "LowRDSFreeStorage"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 1
  metric_name         = "FreeStorageSpace"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 500000000 # 500 MB
  dimensions = {
    DBInstanceIdentifier = aws_db_instance.petclinic_db.id
  }
}

resource "aws_security_group" "petclinic_sg" {
  name = "PetClinicSG"

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = [var.my_ip]
  }

  ingress {
    from_port   = 9966
    to_port     = 9966
    protocol    = "tcp"
    cidr_blocks = [var.my_ip]
  }

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # ingress {
  #   from_port   = 0
  #   to_port     = 65535  # All TCP ports
  #   protocol    = "tcp"
  #   cidr_blocks = [var.my_ip]
  # }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_instance" "petclinic_db" {
  allocated_storage       = 20
  engine                  = "mysql"
  engine_version          = "8.0"
  instance_class          = "db.t4g.micro"
  identifier              = "petclinic-db"
  username                = var.db_username
  password                = var.db_password
  publicly_accessible     = false
  vpc_security_group_ids  = [aws_security_group.petclinic_sg.id]
  skip_final_snapshot     = true
  db_name                 = "petclinic"


  tags = {
    Name = "PetClinicRDS"
  }
}


# resource "aws_security_group_rule" "db_access_from_ec2" {
#   type        = "ingress"
#   from_port   = 3306
#   to_port     = 3306
#   protocol    = "tcp"
#   cidr_blocks = ["${aws_instance.pet_clinic.private_ip}/32"]

#   security_group_id = aws_security_group.petclinic_sg.id

#   depends_on = [aws_instance.pet_clinic]
# }