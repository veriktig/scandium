#
#  Copyright 2018 Veriktig, Inc.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

# fake package that provides JTcl version,
# project.version substituted during build


if { [ catch {
    if {[regexp {^[2-9]} "${project.version}"]} {
        package provide JTcl "${project.version}"	
    } else {
        package provide JTcl 0.0	
    }
  }]} {
    # default if project.version not substituted
    package provide JTcl "2.9.0"
}


