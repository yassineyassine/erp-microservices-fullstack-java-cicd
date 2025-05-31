#!/bin/bash
# Files are ordered in proper order with needed wait for the dependent custom resource definitions to get initialized.
# Usage: bash helm-apply.sh
cs=csvc
suffix=helm
kubectl apply -f namespace.yml
helmVersion=$(helm version --client | grep -E "v3\\.[0-9]{1,3}\\.[0-9]{1,3}" | wc -l)
if [ -d "${cs}-${suffix}" ]; then
  if [ $helmVersion -eq 1 ]; then
helm uninstall ${cs} 2>/dev/null
  else
helm delete --purge ${cs} 2>/dev/null
  fi
helm dep up ./${cs}-${suffix}
  if [ $helmVersion -eq 1 ]; then
helm install ${cs} ./${cs}-${suffix} --replace --namespace erp
  else
helm install --name ${cs} ./${cs}-${suffix} --replace --namespace erp
  fi
fi
  if [ $helmVersion -eq 1 ]; then
helm uninstall product 2>/dev/null
  else
helm delete --purge product 2>/dev/null
  fi
helm dep up ./product-${suffix}
  if [ $helmVersion -eq 1 ]; then
helm install product  ./product-${suffix} --replace --namespace erp
  else
helm install --name product  ./product-${suffix} --replace --namespace erp
  fi
  if [ $helmVersion -eq 1 ]; then
helm uninstall store 2>/dev/null
  else
helm delete --purge store 2>/dev/null
  fi
helm dep up ./store-${suffix}
  if [ $helmVersion -eq 1 ]; then
helm install store  ./store-${suffix} --replace --namespace erp
  else
helm install --name store  ./store-${suffix} --replace --namespace erp
  fi


