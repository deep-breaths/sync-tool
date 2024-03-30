#!/bin/bash
include=()
exclude=()
build=()
profiles=()

cat <<EOF
  **********提示**********
  -i    # 包含模块，只打包这些模块       （使用格式 include,include2,include3 指定，不为空时 -e 参数的值无效，按,分隔）
  -e    # 排除模块，不打包这些模块       （使用格式 exclude1,exclude2,exclude3 指定，按,分隔，默认值为 business-center/dimmer-app,monitor-center/log-center）
  -b    # 环境参数                  （使用格式 仓库或目标服务器,镜像前缀或空# 指定，先按#分隔再按,分隔，默认值为示例值）
  -p    # 环境                       （使用格式 test,dev,prod 指定，test、dev、prod分别为200，45，阿里云，不为空时 -b 参数的值无效）
  示例：sh test.sh -i "include,include2,include3" -e "exclude1,exclude2,exclude3" \
       -b "192.168.30.200#192.168.30.45:5000,iot-platform#registry.cn-shenzhen.aliyuncs.com,private-docker-registry" \
       -p "test,dev,prod"
EOF
# sh test.sh -i "include,include2,include3" -e "exclude1,exclude2,exclude3"
while (("$#")); do
  case "$1" in
  -i)
    IFS=',' read -r -a include <<<"$2" # 用逗号作为分隔符
    shift 2
    ;;
  -e)
    IFS=',' read -r -a exclude <<<"$2" # 用逗号作为分隔符
    shift 2
    ;;
  -b)
    IFS='#' read -r -a build <<<"$2" # 用#作为分隔符
    shift 2
    ;;
  -p)
    IFS=',' read -r -a profiles <<<"$2" # 用逗号作为分隔符
    shift 2
    ;;
  *)
    echo "Error: 无效标志"
    exit 1
    ;;
  esac
done
#exclude为空时设置默认值
if [ ${#exclude[@]} -eq 0 ]; then
  exclude=("business-center/dimmer-app" "monitor-center/log-center")
fi
test="192.168.30.200"
dev="192.168.30.45:5000,iot-platform"
prod="registry.cn-shenzhen.aliyuncs.com,private-docker-registry"
if [ ${#build[@]} -eq 0 ]; then
  build=("$test" "$dev" "$prod")
fi

if [ ${#profiles[@]} -gt 0 ]; then
  build=()
fi

for element in "${profiles[@]}"; do
  if [ "$element" = "test" ]; then
    build+=("$test")
  elif [ "$element" = "dev" ]; then
    build+=("$dev")
  elif [ "$element" = "prod" ]; then
    build+=("$prod")
  fi
done

build_image() {
  local dir=$1
  local repo=$2
  local prefix=$3
  local image_path=$PWD/image
  echo "Building $dir"
  pushd "$dir" >/dev/null
  jar_file=$(ls *.jar 2>/dev/null)
  jar_file=${jar_file%".jar"}
  echo "$branch_name"
  # 执行 mvn help:evaluate 命令获取变量值
  projectArtifactId=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
  # 去除首尾空格和换行符
  projectArtifactId="${projectArtifactId%"${projectArtifactId##*[![:space:]]}"}"
#  build_push=$(mvn docker:build -Ddocker.host=unix:///var/run/docker.sock -DimageName=${repo}/${prefix}/${projectArtifactId}:${branch_name} -Ddocker.repository="$repo" -Ddocker.image.prefix="$prefix" -Ddocker.image.tag=$branch_name -DskipDockerPush)
  mvn docker:build -Ddocker.host=unix:///var/run/docker.sock -DimageName=${projectArtifactId}:${branch_name} -Ddocker.image.name=${prefix}-${projectArtifactId}:${branch_name} -DdockerImageName=${prefix}/${projectArtifactId}:${branch_name} -Ddocker.repository="$repo" -Ddocker.image.prefix="$prefix" -Ddocker.image.tag=$branch_name -DskipDockerPush
  if [ $? -eq 0 ]; then
    docker save -o ${image_path}/${projectArtifactId}.tar ${repo}/${prefix}/${projectArtifactId}:${branch_name}
    echo "********SUCCESS:保存镜像 $jar_file 成功********"
  else
    echo "********ERROR:保存镜像  $jar_file 失败*********"
  fi
  popd >/dev/null
}
build_jar() {
  local dir=$1
  local repo=$2
  local jar_path=$PWD
  echo "Building $dir"
  pushd "$dir" >/dev/null

  repo="${repo%%:*}"
  jar_file=$(ls *.jar 2>/dev/null)
  jar_file=${jar_file%".jar"}
  cp target/*.jar $jar_path/jar/
  if [ $? -eq 0 ]; then
    echo "******SUCCESS:复制$jar_file成功******"
  else
    echo "******ERROR:复制$jar_file失败,路径：$jar_path,命令：cp target/*.jar $PWD/jar/******"
    popd >/dev/null
    return
  fi

  #  # -------------远程重启docker容器：docker需允许远程管理---------
  #  # 检查JAR文件是否存在
  #  if [ -n "$jar_file" ]; then
  #    container_list=$(docker ps -aq)
  #
  #    # 遍历容器列表
  #    for container_id in $container_list; do
  #      # 获取容器名称
  #      container_name=$(docker -H tcp://"$repo":2375 inspect --format='{{.Name}}' "$container_id")
  #
  #      # 去除容器名称前的反斜杠
  #      container_name=${container_name:1}
  #
  #      # 检查容器名称是否包含$jar_file
  #      if [[ $container_name == *"$jar_file"* ]]; then
  #        # 重启容器
  #        echo "重启容器: $container_name"
  #        docker -H tcp://"$repo":2375 restart "$container_id"
  #        if [ $? -eq 0 ]; then
  #          echo "******容器 $container_name 重启命令执行成功******"
  #        else
  #          echo "******容器 $container_name 重启命令执行失败******"
  #        fi
  #        break
  #      fi
  #    done
  #  fi
  #  # -----------------------------------
  popd >/dev/null
}

for elements in "${build[@]}"; do
  IFS=',' read -r value1 value2 <<<"$elements"
  repo="$value1"
  prefix="$value2"
  out_type=""
  if [[ -z $repo ]]; then
    echo "仓库或目标服务器地址不能为空"
    continue
  fi
  if [[ -z $prefix ]]; then
    out_type="jar"
  fi
  # 查找当前文件夹下子文件夹中的Dockerfile
  find . -type f -path "*/src/main/docker/Dockerfile" | while read -r file; do
    # 提取文件路径的目录部分
    dir=$(dirname "$file")
    dir=${dir%"/src/main/docker"}
    # 去除前面的 "./" 部分
    dir=${dir#./}

    if [ ${#include[@]} -eq 0 ]; then
      skip=false
      for element in "${exclude[@]}"; do
        if [[ "$dir" == *"$element"* ]]; then
          skip=true
          break
        fi
      done

      if ! $skip; then
        if [ "$out_type" = "jar" ]; then
          build_jar "$dir" "$repo"
        else
          build_jar "$dir" "$repo"
          build_image "$dir" "$repo" "$prefix"
        fi

      fi

    else
      # 如果数组include为空，则输出每个元素
      for element in "${include[@]}"; do
        echo "$element"
        if [[ "$dir" == *"$element"* ]]; then
          if [ "$out_type" = "jar" ]; then
            build_jar "$dir" "$repo"
          else
            build_jar "$dir" "$repo"
            build_image "$dir" "$repo" "$prefix"
          fi
        fi
      done
    fi
  done
done
