pipeline {
    environment {
        //Change it to your project name
        PROJECT_NAME = "spring-boot-empty-project"

        //Change it to your project git
        PROJECT_GIT_URL = "https://gitlab.com/KonChoo/spring-boot-empty-project.git"

        //Change it to your project deployment branch
        GIT_BRANCH = "master"

        //Change it to your git credentials store in jenkins or not when your project that can be public access
        GIT_CREDENTIALS = ""

        //Change it to your image tag
        IMAGE_TAG = "${BUILD_ID}"

        //Change it to your project Dockerfile
        DOCKERFILE = "Dockerfile"

        //Change it to your k8s deployment file for dev environment
        K8S_DEV_DEPLOYMENT_FILE = "deployment.yaml"
    }
    agent any
    tools {
        gradle 'gradle7'
        jdk 'jdk17'
    }
    stages {
        stage('从Git仓库拉取代码') {
            steps {
                script {
                    println "当前环境变量 : "
                    sh 'env'
                    println "当前目录位置 : "
                    sh 'pwd'
                    println "当前目录位置所有文件 : "
                    sh 'ls'
                    if (env.modules == null || env.modules.trim().isEmpty()) {
                        println("没有选择任何需要部署的项目，退出拉取代码的步骤")
                        currentBuild.result = "SUCCESS"
                        return
                    } else {
                        cleanWs()
                        if (env.GIT_CREDENTIALS == null || env.GIT_CREDENTIALS.trim().isEmpty()) {
                            git branch: "${GIT_BRANCH}",
                                    url: "${PROJECT_GIT_URL}"
                        } else {
                            git branch: "${GIT_BRANCH}",
                                    url: "${PROJECT_GIT_URL}",
                                    credentialsId: "${GIT_CREDENTIALS}"
                        }
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
                        def array = env.modules.split(',')
                        array.each { module ->
                            echo "当前处理 ${module} 中..."
                            if (env.ENVIRONMENT == 'dev') {
                                sh "gradle build -b ${module}/build.gradle -x test"
                                //${JOB_NAME} should be the same as git repo project name
                                sh "docker build --build-arg JAR_FILE=build/libs/*.jar -t 192.168.0.111:8050/${PROJECT_NAME}-${module}:${IMAGE_TAG} -f ${module}/${DOCKERFILE} ${module}"
                                sh "docker push 192.168.0.111:8050/${PROJECT_NAME}-${module}:${IMAGE_TAG}"
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
                                def yaml = readFile("${module}/${K8S_DEV_DEPLOYMENT_FILE}")
                                yaml = yaml.replace('${IMAGE}', "192.168.0.111:8050/${PROJECT_NAME}-${module}:${IMAGE_TAG}")
                                println("当前模块 ${module} 的k8s配置文件内容 : \n")
                                println("${yaml}")
                                writeFile file: "${module}/${K8S_DEV_DEPLOYMENT_FILE}", text: yaml
                                withCredentials([file(credentialsId: "k8s-credentials", variable: 'KUBECONFIG_FILE')]) {
                                    sh "kubectl --kubeconfig=${KUBECONFIG_FILE} apply -f ${module}/${K8S_DEV_DEPLOYMENT_FILE}"
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
