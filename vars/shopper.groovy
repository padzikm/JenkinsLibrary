def test(projectName, testFilePath){
    def imageName = "${projectName}.test.${env.BUILD_ID}"

    stage("build docker image"){
        sh "cd ./${projectName} && docker image build --target test -t ${imageName} ."
    }
    
    stage("run docker image"){
        try{
            sh "docker container run --name ${imageName} ${imageName}"
        }
        catch(ex){
        }
    }
    
    stage("copy test results"){
        sh "docker cp ${imageName}:${testFilePath} testResults.xml"
    }
    
    stage("remove docker image"){
        sh "docker container rm ${imageName}"
    }
    
    stage("verify test results"){
        xunit thresholdMode: 2, thresholds: [failed(failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0')], tools: [xUnitDotNet(deleteOutputFiles: true, failIfNotNew: false, pattern: 'testResults.xml', skipNoTestFiles: false, stopProcessingIfError: true)]
    }
}