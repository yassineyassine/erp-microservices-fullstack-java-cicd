# JHipster-generated Kubernetes configuration

## Preparation

You will need to push your image to a registry. If you have not done so, use the following commands to tag and push the images:

```
$ docker image tag store yassine2912/store
$ docker push yassine2912/store
$ docker image tag invoice yassine2912/invoice
$ docker push yassine2912/invoice
$ docker image tag notification yassine2912/notification
$ docker push yassine2912/notification
$ docker image tag product yassine2912/product
$ docker push yassine2912/product
```

## Deployment

You can deploy all your apps by running the below bash command:

```
./kubectl-apply.sh -f (default option)  [or] ./kubectl-apply.sh -k (kustomize option) [or] ./kubectl-apply.sh -s (skaffold run)
```

If you want to apply kustomize manifest directly using kubectl, then run

```
kubectl apply -k ./
```

If you want to deploy using skaffold binary, then run

```
skaffold run [or] skaffold deploy
```

## Exploring your services

Use these commands to find your application's IP addresses:

```
$ kubectl get svc store -n jhipster
```

## Scaling your deployments

You can scale your apps using:

```
kubectl scale deployment <app-name> --replicas <replica-count> -n jhipster
```

## Zero-downtime deployments

The default way to update a running app in kubernetes, is to deploy a new image tag to your docker registry and then deploy it using:

```
kubectl set image deployment/<app-name>-app <app-name>=<new-image>  -n jhipster
```

Using livenessProbes and readinessProbe allow you to tell Kubernetes about the state of your applications, in order to ensure availability of your services. You will need a minimum of two replicas for every application deployment if you want to have zero-downtime.
This is because the rolling upgrade strategy first stops a running replica in order to place a new. Running only one replica, will cause a short downtime during upgrades.

## Troubleshooting

> my app doesn't get pulled, because of 'imagePullBackof'

Check the docker registry your Kubernetes cluster is accessing. If you are using a private registry, you should add it to your namespace by `kubectl create secret docker-registry` (check the [docs](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/) for more info)

> my applications are stopped, before they can boot up

This can occur if your cluster has low resource (e.g. Minikube). Increase the `initialDelaySeconds` value of livenessProbe of your deployments

> my applications are starting very slow, despite I have a cluster with many resources

The default setting are optimized for middle-scale clusters. You are free to increase the JAVA_OPTS environment variable, and resource requests and limits to improve the performance. Be careful!


#adresse du cluster

kubectl config view --minify -o jsonpath='{.clusters[0].cluster.server}'