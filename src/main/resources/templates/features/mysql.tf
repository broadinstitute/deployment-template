# Cloud SQL database
resource "random_id" "user-password" {
  byte_length   = 16
}

resource "random_id" "root-password" {
  byte_length   = 16
}

module "cloudsql" {
  source        = "github.com/broadinstitute/terraform-shared.git//terraform-modules/cloudsql-mysql?ref=cloudsql-mysql-0.1.1"

  providers {
    google.target =  "google"
  }
  project       = "${var.google_project}"
  cloudsql_name = "${var.owner}-${var.service}-db"
  cloudsql_database_name = "${var.cloudsql_database_name}"
  cloudsql_database_user_name = "${var.cloudsql_app_username}"
  cloudsql_database_user_password = "${random_id.user-password.hex}"
  cloudsql_database_root_password = "${random_id.root-password.hex}"
  cloudsql_instance_labels = {
    "app" = "${var.owner}-${var.service}"
  }
}
