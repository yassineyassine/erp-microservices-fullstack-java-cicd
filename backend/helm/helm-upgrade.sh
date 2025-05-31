#!/bin/bash
# Files are ordered in proper order with needed wait for the dependent custom resource definitions to get initialized.
# Usage: bash helm-apply.sh
cs=csvc
suffix=helm
if [ -d "${cs}-${suffix}" ]; then
helm dep up ./${cs}-${suffix}
helm upgrade --install ${cs} ./${cs}-${suffix} --namespace erp
fi
helm dep up ./product-${suffix}
helm upgrade --install product ./product-${suffix} --namespace erp
helm dep up ./store-${suffix}
helm upgrade --install store ./store-${suffix} --namespace erp


