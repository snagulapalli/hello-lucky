resource "aws_ssm_parameter" "phone_codes" {
  name  = "/hello-lucky/phone-country-codes"
  type  = "String"
  value = jsonencode({ US = 1, INDIA = 91 })
}

resource "aws_secretsmanager_secret" "lucky_numbers" {
  name = "hello-lucky/lucky-numbers"
}

resource "aws_secretsmanager_secret_version" "lucky_numbers" {
  secret_id     = aws_secretsmanager_secret.lucky_numbers.id
  secret_string = jsonencode({ US = 11, INDIA = 22 })
}