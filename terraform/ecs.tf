resource "aws_ecr_repository" "hello_lucky" {
  name         = "hello-lucky"
  force_delete = true
}

resource "aws_cloudwatch_log_group" "hello_lucky" {
  name              = "/ecs/hello-lucky"
  retention_in_days = 7
}

resource "aws_ecs_cluster" "hello_lucky" {
  name = "hello-lucky"
}

resource "aws_ecs_task_definition" "hello_lucky" {
  family                   = "hello-lucky"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.execution.arn
  task_role_arn            = aws_iam_role.task.arn

  runtime_platform {
    cpu_architecture        = "ARM64"
    operating_system_family = "LINUX"
  }

  container_definitions = jsonencode([{
    name      = "hello-lucky"
    image     = "${aws_ecr_repository.hello_lucky.repository_url}:latest"
    essential = true
    portMappings = [{
      containerPort = 8080
      protocol      = "tcp"
    }]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.hello_lucky.name
        awslogs-region        = "us-east-1"
        awslogs-stream-prefix = "ecs"
      }
    }
  }])
}

resource "aws_ecs_service" "hello_lucky" {
  name            = "hello-lucky"
  cluster         = aws_ecs_cluster.hello_lucky.id
  task_definition = aws_ecs_task_definition.hello_lucky.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_subnet.a.id, aws_subnet.b.id]
    security_groups  = [aws_security_group.hello_lucky.id]
    assign_public_ip = true
  }

  wait_for_steady_state = true
}