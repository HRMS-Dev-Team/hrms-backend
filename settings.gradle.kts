rootProject.name = "cre-hrms"

// Apps
include("apps:hrms-auth")
include("apps:hrms-employee")
include("apps:hrms-leave")
include("apps:hrms-salary-advance")

// Libs
include("libs:common")
include("libs:core")
include("libs:dto")
include("libs:persistence")
include("libs:messaging")
include("libs:security")
include("libs:config")
include("libs:test-utils")
