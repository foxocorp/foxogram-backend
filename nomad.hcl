variable "env" {
  type    = string
  default = "dev"
}

locals {
  is_dev = var.env != "prod"

  service_api     = "foxogram-api${local.is_dev ? "-${var.env}" : ""}"
  service_gateway = "foxogram-gateway${local.is_dev ? "-${var.env}" : ""}"

  domain_api     = "api${local.is_dev ? ".${var.env}" : ""}.foxogram.su"
  domain_gateway = "gateway${local.is_dev ? ".${var.env}" : ""}.foxogram.su"
}

job "foxogram-backend" {
  namespace   = var.env
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
          "traefik.enable"                                                      = "true"
          "traefik.http.routers.${local.service_api}.rule"                      = "Host(`${local.domain_api}`)"
          "traefik.http.routers.${local.service_api}.tls.certresolver"          = "letsencrypt"
          "traefik.http.services.${local.service_api}.loadbalancer.server.port" = "8080"
          "traefik.http.routers.${local.service_api}.middlewares"               = "ratelimit@file"
        }
      }

      service {
        name = "api"

        check {
          address_mode   = "driver"
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
          "traefik.enable"                                                          = "true"
          "traefik.http.routers.${local.service_gateway}.rule"                      = "Host(`${local.domain_gateway}`)"
          "traefik.http.routers.${local.service_gateway}.tls.certresolver"          = "letsencrypt"
          "traefik.http.services.${local.service_gateway}.loadbalancer.server.port" = "8080"
          "traefik.http.routers.${local.service_gateway}.middlewares"               = "ratelimit@file"
        }
      }

      service {
        name = "gateway"

        check {
          address_mode   = "driver"
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
