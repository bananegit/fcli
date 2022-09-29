#!/bin/bash -x

SSC_SESSION_NAME=integration-test

checkVars() {
    [[ -z "${FCLI_CMD}" ]] && echo "FCLI_CMD must be set to either 'java -jar path/to/fcli.jar' or path/to/fcli native binary" && exit 1
    [[ -z "${DEMO_SSC_URL}" ]] && echo "DEMO_SSC_URL must be set to SSC demo container URL" && exit 1
    [[ -z "${DEMO_SSC_USER}" ]] && echo "DEMO_SSC_USER must be set to SSC demo container user" && exit 1
    [[ -z "${DEMO_SSC_PWD}" ]] && echo "DEMO_SSC_PWD must be set to SSC demo container user password" && exit 1
}

sscSessionCmd() {
    sscCmd "$@" --session ${SSC_SESSION_NAME}
}

sscCmd() {
    runCmd ${FCLI_CMD} ssc "$@"
}

runCmd() {
    echo "$@"
    lastOutput=$("$@") || exit 1 
    if [[ -v checkOutput[@] ]]; then
        echo "$lastOutput" | "${checkOutput[@]}" || exit 1
        unset checkOutput
    fi
}

runPersistentSessionChecks() {
    checkOutput=(fgrep ${SSC_SESSION_NAME}); sscCmd session login ${SSC_SESSION_NAME} --url ${DEMO_SSC_URL} -u${DEMO_SSC_USER} -p${DEMO_SSC_PWD}
    
    runPersistentTestCommands
    sscCmd session logout ${SSC_SESSION_NAME} -u${DEMO_SSC_USER} -p${DEMO_SSC_PWD}
}

runTransientSessionChecks() {
    FCLI_SSC_URL=${DEMO_SSC_URL} FCLI_SSC_USER=${DEMO_SSC_USER} FCLI_SSC_PASSWORD=${DEMO_SSC_PWD} runTransientTestCommands
}

runTransientTestCommands() {
    # We just run a single command to test transient session management
    # No need to repeat all commands from persistent session test
    sscCmd app list
}

runPersistentTestCommands() {
    sscSessionCmd activity-feed list
    sscSessionCmd alert-definition list
    sscSessionCmd alert list
    sscSessionCmd app list
    sscSessionCmd appversion list
    checkOutput=(fgrep DevPhase); sscSessionCmd attribute-definition list
    sscSessionCmd user list
    sscSessionCmd event list
    sscSessionCmd issue-template list
    sscSessionCmd job list
    sscSessionCmd plugin list
    checkOutput=(fgrep "OWASP Top 10"); sscSessionCmd report-template list
    checkOutput=(fgrep Administrator); sscSessionCmd role list
    checkOutput=(fgrep projectversion_add); sscSessionCmd role-permission list
    checkOutput=(fgrep CIToken); sscSessionCmd token-definition list
    
    checkOutput=(fgrep CIToken); sscCmd token create CIToken -u${DEMO_SSC_USER} -p${DEMO_SSC_PWD} --expire-in 5m --fields "id#type#restToken"
    restToken=$(echo $lastOutput | awk '{print $3}')
    sscCmd token revoke $restToken -u${DEMO_SSC_USER} -p${DEMO_SSC_PWD}
}

run() {
    checkVars
    runTransientSessionChecks
    runPersistentSessionChecks
}

run