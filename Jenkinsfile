pipeline {
    agent any
    tools {
        gradle 'gradle7'
        jdk 'jdk17'
    }
    stages {
        stage('从Git仓库拉取代码') {
            steps {
                script {
                    if (env.modules == null || env.modules.trim().isEmpty()) {
                        println("没有选择任何需要部署的项目，退出拉取代码的步骤")
                        currentBuild.result = "SUCCESS"
                        return
                    } else {
                        cleanWs()
                        git branch: 'master', url: 'https://gitlab.com/KonChoo/spring-boot-empty-project.git'
                    }
                }
            }
        }
        stage('构建项目的Docker镜像') {
            steps {
                script {
                    if (env.modules == null || env.modules.trim().isEmpty()) {
                        println("没有选择任何需要部署的项目，退出Docker镜像构建步骤")
                        currentBuild.result = "SUCCESS"
                        return
                    } else {
                        sh 'env'
                        sh 'pwd'
                        sh 'ls'
                        def array = env.modules.split(',')
                        array.each { module ->
                            echo "当前处理 ${module} 中..."
                            if (env.ENVIRONMENT == 'dev') {
                                sh "gradle build -b ${module}/build.gradle -x test"
                                //${JOB_NAME} should be the same as git repo project name
                                sh "docker build --build-arg JAR_FILE=build/libs/*.jar -t 192.168.0.111:8050/${JOB_NAME}-${module}:${BUILD_ID} ${module}"
                                sh "docker push 192.168.0.111:8050/${JOB_NAME}-${module}:${BUILD_ID}"
                            } else {
                                println "当前选择的环境待实现..."
                                currentBuild.result = "FAILURE"
                                return
                            }
                        }
                    }
                }
            }
        }
        stage('部署项目到K8s集群') {
            steps {
                script {
                    if (env.modules == null || env.modules.trim().isEmpty()) {
                        println("没有选择任何需要部署的项目 ，退出K8s部署的步骤")
                        currentBuild.result = "SUCCESS"
                        return
                    } else {
                        def array = env.modules.split(',')
                        array.each { module ->
                            println("当前部署到K8s的模块 ： ${module}")
                            if (env.ENVIRONMENT == "dev") {
                                def yaml = readFile("${module}/deployment.yaml")
                                yaml = yaml.replace('${IMAGE}', "192.168.0.111:8050/${JOB_NAME}-${module}:${BUILD_ID}")
                                println("当前模块 ${module} 的k8s配置文件内容 : \n")
                                println("${yaml}")
                                writeFile file: "${module}/deployment.yaml", text: yaml
                                withCredentials([file(credentialsId: "k8s-credentials", variable: 'KUBECONFIG_FILE')]) {
                                    // sh 'kubectl --kubeconfig="${KUBECONFIG_FILE}" set image deployment/spring-boot-empty-project-deployment spring-boot-empty-project=192.168.0.111:8050/spring-boot-empty-project:${BUILD_ID}'
                                    sh "kubectl --kubeconfig=${KUBECONFIG_FILE} apply -f ${module}/deployment.yaml"
                                }
                            } else {
                                println "当前选择的环境待实现..."
                                currentBuild.result = "FAILURE"
                                return
                            }
                        }
                    }
                }
            }
        }
    }
}
