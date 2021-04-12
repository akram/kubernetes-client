/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.kubernetes.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.openshift.api.model.ImageStream;

public class TripleMultipleSharedInformerExample {
    private static final long RESYNC = 0L;
    private static final Logger logger = LoggerFactory.getLogger(TripleMultipleSharedInformerExample.class);
    private static final String NAMESPACE_PREFIX = "multi-";
    private static SharedInformerFactory INFORMER;

    public static void main(String[] args) throws InterruptedException {
        KubernetesClient client = new DefaultKubernetesClient();
        for (int i = 10; i < 100; i++) {
            newPodSharedInformerClient(client, NAMESPACE_PREFIX + i);
            newConfigMapSharedInformerClient(client, NAMESPACE_PREFIX + i);
            newImageStreamSharedInformerClient(client, NAMESPACE_PREFIX + i);
        }
        SharedInformerFactory factory = getInformer(client);
        factory.startAllRegisteredInformers();
    }

    private static void newPodSharedInformerClient(KubernetesClient client, String namespace)
            throws InterruptedException {
        SharedInformerFactory factory = getInformer(client).inNamespace(namespace);
        SharedIndexInformer<Pod> informer = factory.sharedIndexInformerFor(Pod.class, RESYNC);
        logger.info("Registred pod informer for namespace: " + namespace);
        PodEventHandler eventHandler = new PodEventHandler(namespace);
        informer.addEventHandler(eventHandler);
//        new Lister<>(informer.getIndexer(), namespace);
    }

    private static SharedInformerFactory getInformer(KubernetesClient client) {
        if (INFORMER == null) {
            INFORMER = client.informers();
        }
        return INFORMER;
    }

    private static void newConfigMapSharedInformerClient(KubernetesClient client, String namespace)
            throws InterruptedException {
        SharedInformerFactory factory = getInformer(client).inNamespace(namespace);
        SharedIndexInformer<ConfigMap> informer = factory.sharedIndexInformerFor(ConfigMap.class, RESYNC);
        logger.info("Registred configmap informer for namespace: " + namespace);
        ConfigMapEventHandler eventHandler = new ConfigMapEventHandler(namespace);
        informer.addEventHandler(eventHandler);
        factory.startAllRegisteredInformers();
//        new Lister<>(informer.getIndexer(), namespace);
    }

    private static void newImageStreamSharedInformerClient(KubernetesClient client, String namespace)
            throws InterruptedException {
        SharedInformerFactory factory = getInformer(client).inNamespace(namespace);
        SharedIndexInformer<ImageStream> informer = factory.sharedIndexInformerFor(ImageStream.class, RESYNC);
        logger.info("Registred imagestream informer for namespace: " + namespace);
        ImageStreamEventHandler eventHandler = new ImageStreamEventHandler(namespace);
        informer.addEventHandler(eventHandler);
//        new Lister<>(informer.getIndexer(), namespace);
    }

    private static final class PodEventHandler implements ResourceEventHandler<Pod> {
        public String namespace;

        public PodEventHandler(String namespace) {
            super();
            this.namespace = namespace;
        }

        @Override
        public void onAdd(Pod obj) {
            logger.info("{} pod added", obj.getMetadata().getName());
        }

        @Override
        public void onUpdate(Pod oldObj, Pod newObj) {
            logger.info("{} pod updated in {}", oldObj.getMetadata().getName(), namespace);
        }

        @Override
        public void onDelete(Pod obj, boolean deletedFinalStateUnknown) {
            logger.info("{} pod deleted", obj.getMetadata().getName());
        }
    }

    private static final class ConfigMapEventHandler implements ResourceEventHandler<ConfigMap> {
        public String namespace;

        public ConfigMapEventHandler(String namespace) {
            super();
            this.namespace = namespace;
        }

        @Override
        public void onAdd(ConfigMap obj) {
            logger.info("{} ConfigMap added in {}", obj.getMetadata().getName(), namespace);
        }

        @Override
        public void onUpdate(ConfigMap oldObj, ConfigMap newObj) {
            logger.info("{} ConfigMap updated in {}", newObj.getMetadata().getName(), namespace);
//            logger.info("old was {}", oldObj, namespace);
        }

        @Override
        public void onDelete(ConfigMap obj, boolean deletedFinalStateUnknown) {
            logger.info("{} ConfigMap deleted in {}", obj.getMetadata().getName(), namespace);
        }
    }

    private static final class ImageStreamEventHandler implements ResourceEventHandler<ImageStream> {
        public String namespace;

        public ImageStreamEventHandler(String namespace) {
            super();
            this.namespace = namespace;
        }

        @Override
        public void onAdd(ImageStream obj) {
            logger.info("{} ImageStream added", obj.getMetadata().getName());
        }

        @Override
        public void onUpdate(ImageStream oldObj, ImageStream newObj) {
            logger.info("{} ImageStream updated in {}", oldObj.getMetadata().getName(), namespace);
        }

        @Override
        public void onDelete(ImageStream obj, boolean deletedFinalStateUnknown) {
            logger.info("{} ImageStream deleted", obj.getMetadata().getName());
        }
    }
    
    
    
    
    
    
    

}
