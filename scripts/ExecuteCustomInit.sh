#!/usr/bin/env bash

usage="Helper script to initialize custom projects

Arguments:
    -t | --project-type     : required : set project type to be built, available templates type:
                                            - java-dropwizard
                                            - scala-gradle-play
                                            - custom

    -s | --project-target   : optional : set destination directory where the project will be created. Default will be
                                         set to the directory where the script is being called from

    -c | --custom-template  : optional : set a custom template source project. For now only github is supported.
                                         in case of a nested project you will have to indicate the path to the source
                                         directory that contains the templates for the project. Though the template
                                         directory has to have a predefined structure e.g.:

                                         Lets assume FOLDER_B contains the template we want to copy, then FOLDER_B should
                                         have 2 subdirectories called 'script' and 'template'

                                         script -> contains a file named 'CustomInit.properties' this file will contain
                                                   all the parameters required to process the project files, this means
                                                   that in your project files you can replace anything with a GString macro
                                                   and it will be replaced with the proper value defined in the .properties file

                                         template -> put all the project files you want to be copied to the destination
                                                     directory defined by '-s | --project-target'

                                         Fig. 1 - folder structure
                                         FOLDER_A
                                               |
                                               +--FOLDER_B
                                                       |
                                                       +-- script
                                                       |       |
                                                       |       +-- CustomInit.properties
                                                       |
                                                       +-- template
                                                               |
                                                               +-- your project

    -d | --custom-template-target : optional : this properties is required if --custom-template implies a nested folder structure
                                               like in Fig. 1. In this case this property will point to FOLDER_B e.g:

                                               take a look at Fig.1 so in that case this property is set as follow

                                               --custom-template-source 'FOLDER_A/FOLDER_B'

    -l | --library-version-source   : optional : set the source to a .propeties file to override default provided properties file.
                                                 Useful when you want to replace predefined library versions

    -g | --group                    : optional : set group parameter in case of a gradle project. Default --project-target directory will be used

    -p | --project-structure        : optional : set a project structure to be created when building a project

    --debug                         : optional : enable debug information for this script and gradle execution

    --stacktrace                    : optional : enables stacktrace for gradle execution

    -h : display this message

    For more details on how the plugin works checkout https://github.com/maxbalan/gradle-custom-init
    "

# set default project source to current directory where the script is called
# if you wanna set a different one execute this script with parameter "-s"
PROJECT_TARGET=`pwd`
GRADLE_HOME=$GRADLE_HOME
DEBUG=false
STACKTRACE=false

if [ -z "$GRADLE_HOME" ]; then
    echo "GRADLE_HOME not found. Please set env variable GRADLE_HOME"
    exit 1
fi

while true; do
  case "$1" in
    -h | --help) echo "$usage"; exit 0;;
    -s | --project-target) PROJECT_TARGET=$2; shift 2;;
    -t | --project-type) PROJECT_TYPE=$2; shift 2;;
    -c | --custom-template) CUSTOM_TEMPLATE=$2; shift 2;;
    -d | --custom-template-target) CUSTOM_TEMPLATE_TARGET=$2; shift 2;;
    -l | --library-version-source) CUSTOM_PROPERTIES=$2; shift 2;;
    -g | --group) GROUP=$2; shift 2;;
    -p | --project-structure) PROJECT_STRUCTURE=$2; shift 2;;
    --debug) DEBUG=true; shift 1;;
    --stacktrace) STACKTRACE=true shift 1;;
    -- ) shift; break ;;
    * ) break ;;
  esac
done


if [ "$DEBUG" = true ] ; then
    echo "PROJECT_TARGET= $PROJECT_TARGET"
    echo "PROJECT_TYPE = $PROJECT_TYPE"
    echo "CUSTOM_TEMPLATE = $CUSTOM_TEMPLATE"
    echo "CUSTOM_TEMPLATE_SOURCE = $CUSTOM_TEMPLATE_TARGET"
    echo "CUSTOM_PROPERTIES = $CUSTOM_PROPERTIES"
    echo "GROUP = $GROUP"
    echo "PROJECT_STRUCTURE = $PROJECT_STRUCTURE"
fi

if [ -z "$PROJECT_TYPE" ]; then
    echo "project-type was not set. Please set argument -t | --project-type"
    exit 2
fi

echo "Using project source: $PROJECT_TARGET"

GRADLE_PLUGIN="$PROJECT_TARGET/temp/gradle-custom-init"

if [ ! -f $GRADLE_PLUGIN/build.gradle ]; then
    echo "initializing gradle-custom-init plugin $GRADLE_PLUGIN"
    echo `install -dv $GRADLE_PLUGIN`

    echo "plugins {
            id 'com.github.maxbalan.gradle-custom-init' version '1.3.1'
    }" >> "$GRADLE_PLUGIN/build.gradle"

    echo "" >> "$GRADLE_PLUGIN/settings.build"
fi

moveToPluginDir() {
    echo "changing dir to $GRADLE_PLUGIN"
    cd $GRADLE_PLUGIN
}

moveToPluginDir

cmd="gradle custom-init --project-type $PROJECT_TYPE --project-target $PROJECT_TARGET"

if [ ! -z "$CUSTOM_TEMPLATE" ]; then
   cmd="$cmd --custom-template $CUSTOM_TEMPLATE"
fi

if [ ! -z "$CUSTOM_TEMPLATE_TARGET" ]; then
   cmd="$cmd --custom-template-target $CUSTOM_TEMPLATE_TARGET"
fi

if [ ! -z "$CUSTOM_PROPERTIES" ]; then
   cmd="$cmd --properties $CUSTOM_PROPERTIES"
fi

if [ ! -z "$GROUP" ]; then
   cmd="$cmd --group $GROUP"
fi

if [ ! -z "$PROJECT_STRUCTURE" ]; then
   cmd="$cmd --project-structure $PROJECT_STRUCTURE"
fi

if [ "$STACKTRACE" = true ] ; then
   cmd="$cmd --stacktrace"
fi

if [ "$DEBUG" = true ] ; then
    cmd="$cmd --debug"
fi

if [ "$DEBUG" = true ] ; then
    echo "RUN $cmd"
fi

echo `$cmd`

#cleanup don't be lazy
if [ -d "$PROJECT_TARGET/temp" ]; then
  echo `rm -rf $PROJECT_TARGET/temp`
fi

echo "Execution Successfully"


