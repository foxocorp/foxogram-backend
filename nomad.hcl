variable "service_name_api" {
  type    = string
  default = "foxogram-api-dev"
}

variable "domain_api" {
  type    = string
  default = "api.dev.foxogram.su"
}

variable "service_name_gateway" {
  type    = string
  default = "foxogram-gateway-dev"
}

variable "domain_gateway" {
  type    = string
  default = "gateway.dev.foxogram.su"
}

variable "env" {
  type    = string
  default = "dev"
}

job "foxogram-backend" {
  datacenters = ["dc1"]

  update {
    max_parallel     = 1
    min_healthy_time = "20s"
    healthy_deadline = "2m"
    stagger          = "30s"
    auto_revert      = true
  }

  group "api" {
    network {
      port "http" {
        to = 8080
      }
    }

    task "api" {
      driver = "docker"

      config {
        image        = "foxogram/api:${var.env}"
        network_mode = "foxogram"
        labels = {
          "traefik.enable"                                                         = "true"
          "traefik.http.routers.${var.service_name_api}.rule"                      = "Host(`${var.domain_api}`)"
          "traefik.http.routers.${var.service_name_api}.tls.certresolver"          = "letsencrypt"
          "traefik.http.services.${var.service_name_api}.loadbalancer.server.port" = "8080"
          "traefik.http.routers.${var.service_name_api}.middlewares"               = "ratelimit@file"
        }
      }

      service {
        name = "api"

        check {
          port           = "http"
          name           = "health"
          type           = "http"
          path           = "/actuator/health"
          interval       = "30s"
          timeout        = "10s"
          initial_status = "critical"
        }
      }

      restart {
        attempts = 3
        interval = "10m"
        delay    = "15s"
        mode     = "fail"
      }

      resources {
        cpu    = 500
        memory = 1024
      }
    }
  }

  group "gateway" {
    network {
      port "http" {
        to = 8080
      }
    }

    task "gateway" {
      driver = "docker"

      config {
        image        = "foxogram/gateway:${var.env}"
        network_mode = "foxogram"
        labels = {
          "traefik.enable"                                                             = "true"
          "traefik.http.routers.${var.service_name_gateway}.rule"                      = "Host(`${var.domain_gateway}`)"
          "traefik.http.routers.${var.service_name_gateway}.tls.certresolver"          = "letsencrypt"
          "traefik.http.services.${var.service_name_gateway}.loadbalancer.server.port" = "8080"
          "traefik.http.routers.${var.service_name_gateway}.middlewares"               = "ratelimit@file"
        }
      }

      service {
        name = "gateway"

        check {
          port           = "http"
          name           = "health"
          type           = "http"
          path           = "/actuator/health"
          interval       = "30s"
          timeout        = "10s"
          initial_status = "critical"
        }
      }

      restart {
        attempts = 3
        interval = "10m"
        delay    = "15s"
        mode     = "fail"
      }

      resources {
        cpu    = 500
        memory = 1024
      }
    }
  }
}
