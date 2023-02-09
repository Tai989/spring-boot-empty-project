pipeline {
    agent any
    tools {
        gradle 'gradle7'
        jdk 'jdk17'
    }
    stages {
        stage('从Git仓库拉取代码') {
            steps {
                cleanWs()
                git branch: 'master',
                        url: 'https://gitlab.com/KonChoo/spring-boot-empty-project.git'
            }
        }
        stage('构建项目的Docker镜像') {
            steps {
                sh 'env'
                sh 'pwd'
                sh 'ls'
                sh 'docker --version'
                script {
                    def array = env.modules.split(',')
                    array.each { module ->
                        echo "当前处理 ${module} 中..."
                        if (params.ENVIRONMENT == 'dev') {
                            sh "gradle build -b ${module}/build.gradle -x test"
                            //${JOB_NAME} should be the same as git repo project name
                            sh "docker build --build-arg JAR_FILE=build/libs/*.jar -t 192.168.0.111:8050/${JOB_NAME}-${module}:${BUILD_ID} ${module}"
                            sh "docker push 192.168.0.111:8050/${JOB_NAME}-${module}:${BUILD_ID}"
                        } else {
                            println "当前选择的环境待实现"
                        }
                    }
                }
            }
        }
        stage('部署项目到K8s集群') {
            steps {
                script {
                    def array = env.modules.split(',')
                    array.each { module ->
                        def yaml = readFile("${module}/deployment.yaml")
                        yaml = yaml.replace('${IMAGE}', "192.168.0.111:8050/${JOB_NAME}-${module}:${BUILD_ID}")
                        println("当前模块 ${module} 的k8s配置文件内容 : \n")
                        println("${yaml}")
                        writeFile file: "${module}/deployment.yaml", text: yaml
                        withCredentials([file(credentialsId: "k8s-credentials", variable: 'KUBECONFIG_FILE')]) {
                            // sh 'kubectl --kubeconfig="${KUBECONFIG_FILE}" set image deployment/spring-boot-empty-project-deployment spring-boot-empty-project=192.168.0.111:8050/spring-boot-empty-project:${BUILD_ID}'
                            sh "kubectl --kubeconfig=${KUBECONFIG_FILE} apply -f ${module}/deployment.yaml"
                        }
                    }
                }
            }
        }
    }
}
