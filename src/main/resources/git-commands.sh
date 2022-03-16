#!/bin/sh

REPO_DIR=$1
REPO_NAME=$2
BRANCH_DESTINY=$3
USERNAME=$4
ACCESS_TOKEN=$5
REPOSITORY=github.com/${USERNAME}/${REPO_NAME}.git

echo "############################"
echo "### Init push repository ###"
echo ">> Path local repository: $REPO_DIR"
echo ">> Repository name: $REPO_NAME"
echo ">> Remote branch: $BRANCH_DESTINY"
echo ">> User name: $USERNAME"
echo ">> Repository address: $REPOSITORY"
echo "############################"
echo

cd ${REPO_DIR}

GIT=`which git`
${GIT}

${GIT} remote add origin https://${REPOSITORY}
${GIT} push https://${USERNAME}:${ACCESS_TOKEN}@${REPOSITORY} --all

echo

echo "Push repository complete."
echo "Go to repository https://${REPOSITORY}"
