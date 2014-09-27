#!/bin/bash

usage()
{
    echo "Usage: ${0##*/} {setup|update} {local|remote}"
    exit 1
}

setup_env()
{
case "$ENV" in
    local)
        ansible-playbook -vvvv -i inventory/local.ini --private-key=~/.vagrant.d/insecure_private_key -u vagrant wordie-setup.yaml
        ;;
    remote)
        ansible-playbook -vvvv -i inventory/remote.ini -u cloudsigma wordie-setup.yaml
        ;;
    *)
        usage
        ;;
esac
}

update_env()
{
case "$ENV" in
    local)
        ansible-playbook -vvvv -i inventory/local.ini --private-key=~/.vagrant.d/insecure_private_key -u vagrant wordie-update.yaml
        ;;
    remote)
        ansible-playbook -vvvv -i inventory/remote.ini -u cloudsigma wordie-update.yaml
        ;;
    *)
        usage
        ;;
esac
}

ACTION=$1
ENV=$2

case "$ACTION" in

    setup)
        setup_env
        ;;
    update)
        update_env
        ;;
    *)
        usage
        ;;
esac

exit 0


