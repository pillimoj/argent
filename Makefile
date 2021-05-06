IMAGE := eu.gcr.io/grimsborn/argent
REVISION := $(shell git describe --always --dirty='-dirty')
BRANCH := $(shell git symbolic-ref --short HEAD)
TAG := $(subst /,-,$(BRANCH))-$(REVISION)

# ================= SERVICE ==================
database:
	docker-compose up -d	
	
buildlocalimage:
	./gradlew jibDockerBuild -PgitHash=${TAG} --info

buildproductionimage:
	./gradlew jib -PjibImage=${IMAGE} -PgitHash=${TAG} --info

rundocker:
	$(eval IMAGE_DIGEST := $(shell jq -r .image build/jib-image.json)) \
	docker run -t \
	--env-file .env \
	-v ${PWD}/localdockersecrets:/localsecrets \
	-p 8008:8008 \
	${IMAGE_DIGEST}  | node logparse.js

deploy: buildproductionimage
	$(eval IMAGE_DIGEST := $(shell jq -r .image build/jib-image.json)@$(shell cat build/jib-image.digest))
	gcloud run deploy argent --image=${IMAGE_DIGEST} --platform=managed --region=europe-west1
