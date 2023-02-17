pipeline {
    agent any
    parameters {
        gitParameter branchFilter: '*origin/(.\\*)* ', defaultValue: 'master', name: 'BRANCH', type: 'PT_BRANCH'
    }
    environment {
        //访问git所需要的凭证（先添加到Jenkins中再在这里引用），不需要凭证访问可以不填
        GIT_CREDENTIALS = ""

        //Docker构建镜像的tag 可以使用环境变量如${BUILD_ID}或者固定latest也行
        IMAGE_TAG = "${BUILD_ID}"

        //打包时使用的Dockerfile文件位置 例如 Dockerfile project1/Dockerfile project2/Dockerfile-Dev 等 不要以/开始 直接以文件夹名称开始即可
        DOCKERFILE = "Dockerfile"

        //不是K8s时使用deployment.yaml(部署配置文件)文件位置 例如 deployment.yaml project1/deployment.yaml project2/deployment-dev.yaml 等 不要以/开始 直接以文件夹名称开始即可
        K8S_DEV_DEPLOYMENT_FILE = "deployment.yaml"
    }
    tools {
        //当前构建使用的工具如gradle7 jdk17 需要Jenkins中 "全局工具配置" 那里配置好再按名称在这里引用
        //当前构建使用gradle7
        gradle 'gradle7'
        //当前构建使用jdk17
        jdk 'jdk17'
    }
    stages {
        stage('从Git仓库拉取代码') {
            steps {
                script {
                    echo "当前环境变量 : "
                    sh 'env'
                    echo "当前目录位置 : "
                    sh 'pwd'
                    echo "当前目录位置所有文件 : "
                    sh 'ls'
                    cleanWs()
                  /*  if (env.modules == null || env.modules.trim().isEmpty()) {
                        echo "没有选择任何需要部署的项目，退出拉取代码"
                        currentBuild.result = "SUCCESS"
                        return
                    } else {
                        cleanWs()
                        if (env.GIT_CREDENTIALS == null || env.GIT_CREDENTIALS.trim().isEmpty()) {
                            git branch: "${params.BRANCH}",
                                    url: "${GIT_URL}"
                        } else {
                            git branch: "${params.BRANCH}",
                                    url: "${GIT_URL}",
                                    credentialsId: "${GIT_CREDENTIALS}"
                        }
                    }*/
                }
            }
        }
        stage('构建项目的Docker镜像') {
            steps {
                script {
                    if (env.modules == null || env.modules.trim().isEmpty()) {
                        echo "没有选择任何需要部署的项目，退出Docker镜像构建"
                        currentBuild.result = "SUCCESS"
                        return
                    } else {
                        def array = env.modules.split(',')
                        array.each { module ->
                            echo "docker当前构建项目 ${module} 中..."
                            if (env.ENVIRONMENT == 'dev') {
                                //使用gradle打包并跳过所有test
                                //sh "gradle build -b ${module}/build.gradle -x test"
                                sh "${module}/gradlew bootJar -Dspring.profiles.active=${env.ENVIRONMENT}"
                                //使用docker构建镜像
                                sh "docker build -t 192.168.0.111:8050/${JOB_NAME}-${module}:${IMAGE_TAG} -f ${module}/${DOCKERFILE} ${module}"
                                //推送镜像到内网服务器上docker运行的registry2
                                sh "docker push 192.168.0.111:8050/${JOB_NAME}-${module}:${IMAGE_TAG}"
                            } else {
                                echo "当前选择的环境待实现..."
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
                        echo "没有选择任何需要部署的项目 ，退出K8s部署"
                        currentBuild.result = "SUCCESS"
                        return
                    } else {
                        def array = env.modules.split(',')
                        array.each { module ->
                            echo "当前部署到K8s的项目 ： ${module}"
                            if (env.ENVIRONMENT == "dev") {
                                //读取k8s部署文件
                                def yaml = readFile("${module}/${K8S_DEV_DEPLOYMENT_FILE}")
                                //替换k8s的镜像名称
                                yaml = yaml.replace('${IMAGE}', "192.168.0.111:8050/${JOB_NAME}-${module}:${IMAGE_TAG}")
                                echo "当前部署 ${module} 的k8s配置文件 : \n"
                                echo "${yaml}"
                                //输出替换镜像名称后的k8s配置文件
                                writeFile file: "${module}/${K8S_DEV_DEPLOYMENT_FILE}", text: yaml
                                withCredentials([file(credentialsId: "k8s-credentials", variable: 'KUBECONFIG_FILE')]) {
                                    //根据配置文件部署到集群
                                    sh "kubectl --kubeconfig=${KUBECONFIG_FILE} apply -f ${module}/${K8S_DEV_DEPLOYMENT_FILE}"
                                }
                            } else {
                                echo "当前选择的环境待实现..."
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
