rootProject.name = "groubing-server"

include("boot:gb-boot-web")
include("config:gb-config-yaml-importer")
include("config:gb-config-logging")
include("infrastructure:support:gb-jwt-core")
include("infrastructure:support:gb-fcm-sender")
