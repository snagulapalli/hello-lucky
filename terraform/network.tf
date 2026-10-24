resource "aws_vpc" "hello_lucky" {
  cidr_block           = "10.20.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "hello-lucky"
  }
}

resource "aws_internet_gateway" "hello_lucky" {
  vpc_id = aws_vpc.hello_lucky.id

  tags = {
    Name = "hello-lucky"
  }
}

resource "aws_subnet" "a" {
  vpc_id            = aws_vpc.hello_lucky.id
  cidr_block        = "10.20.1.0/24"
  availability_zone = "us-east-1a"

  tags = {
    Name = "hello-lucky-a"
  }
}

resource "aws_subnet" "b" {
  vpc_id            = aws_vpc.hello_lucky.id
  cidr_block        = "10.20.2.0/24"
  availability_zone = "us-east-1b"

  tags = {
    Name = "hello-lucky-b"
  }
}

resource "aws_route_table" "hello_lucky" {
  vpc_id = aws_vpc.hello_lucky.id

  tags = {
    Name = "hello-lucky"
  }
}

resource "aws_route" "internet" {
  route_table_id         = aws_route_table.hello_lucky.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.hello_lucky.id
}

resource "aws_route_table_association" "a" {
  subnet_id      = aws_subnet.a.id
  route_table_id = aws_route_table.hello_lucky.id
}

resource "aws_route_table_association" "b" {
  subnet_id      = aws_subnet.b.id
  route_table_id = aws_route_table.hello_lucky.id
}

resource "aws_security_group" "hello_lucky" {
  name        = "hello-lucky"
  description = "hello-lucky HTTP 8080"
  vpc_id      = aws_vpc.hello_lucky.id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "hello-lucky"
  }
}