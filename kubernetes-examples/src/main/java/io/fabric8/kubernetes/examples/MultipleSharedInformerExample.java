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
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.kubernetes.client.informers.cache.Lister;

public class MultipleSharedInformerExample {
    private static final Logger logger = LoggerFactory.getLogger(MultipleSharedInformerExample.class);
    private static final String NAMESPACE_PREFIX = "multi-";

    public static void main(String[] args) throws InterruptedException {
        KubernetesClient client = new DefaultKubernetesClient();
        for (int i = 10; i < 100; i++) {
            newPodSharedInformerClient(client, NAMESPACE_PREFIX + i);
            newConfigMapSharedInformerClient(client, NAMESPACE_PREFIX + i);
            newSecretSharedInformerClient(client, NAMESPACE_PREFIX + i);
        }
    }

    private static void newPodSharedInformerClient(KubernetesClient client, String namespace)
            throws InterruptedException {
        SharedInformerFactory factory = client.informers().inNamespace(namespace);
        SharedIndexInformer<Pod> informer = factory.sharedIndexInformerFor(Pod.class, 30 * 1000L);
        logger.info("Informer factory initialized for namesapce: " + namespace);
        PodEventHandler eventHandler = new PodEventHandler();
        informer.addEventHandler(eventHandler);
        logger.info("Starting all registered informers");
        factory.startAllRegisteredInformers();
        new Lister<>(informer.getIndexer(), namespace);
        factory.startAllRegisteredInformers();
    }

    private static void newConfigMapSharedInformerClient(KubernetesClient client, String namespace)
            throws InterruptedException {
        SharedInformerFactory factory = client.informers().inNamespace(namespace);
        SharedIndexInformer<ConfigMap> informer = factory.sharedIndexInformerFor(ConfigMap.class, 30 * 1000L);
        logger.info("Informer factory initialized for namesapce: " + namespace);
        ConfigMapEventHandler eventHandler = new ConfigMapEventHandler();
        informer.addEventHandler(eventHandler);
        logger.info("Starting all registered informers");
        factory.startAllRegisteredInformers();
        new Lister<>(informer.getIndexer(), namespace);
        factory.startAllRegisteredInformers();
    }

    private static void newSecretSharedInformerClient(KubernetesClient client, String namespace)
            throws InterruptedException {
        SharedInformerFactory factory = client.informers().inNamespace(namespace);
        SharedIndexInformer<Secret> informer = factory.sharedIndexInformerFor(Secret.class, 30 * 1000L);
        logger.info("Informer factory initialized for namesapce: " + namespace);
        SecretEventHandler eventHandler = new SecretEventHandler();
        informer.addEventHandler(eventHandler);
        logger.info("Starting all registered informers");
        factory.startAllRegisteredInformers();
        new Lister<>(informer.getIndexer(), namespace);
        factory.startAllRegisteredInformers();
    }

    private static final class PodEventHandler implements ResourceEventHandler<Pod> {
        @Override
        public void onAdd(Pod obj) {
            logger.info("{} pod added", obj.getMetadata().getName());
        }

        @Override
        public void onUpdate(Pod oldObj, Pod newObj) {
            logger.info("{} pod updated", oldObj.getMetadata().getName());
        }

        @Override
        public void onDelete(Pod obj, boolean deletedFinalStateUnknown) {
            logger.info("{} pod deleted", obj.getMetadata().getName());
        }
    }

    private static final class ConfigMapEventHandler implements ResourceEventHandler<ConfigMap> {
        @Override
        public void onAdd(ConfigMap obj) {
            logger.info("{} ConfigMap added", obj.getMetadata().getName());
        }

        @Override
        public void onUpdate(ConfigMap oldObj, ConfigMap newObj) {
            logger.info("{} ConfigMap updated", oldObj.getMetadata().getName());
        }

        @Override
        public void onDelete(ConfigMap obj, boolean deletedFinalStateUnknown) {
            logger.info("{} ConfigMap deleted", obj.getMetadata().getName());
        }
    }

    private static final class SecretEventHandler implements ResourceEventHandler<Secret> {
        @Override
        public void onAdd(Secret obj) {
            logger.info("{} Secret added", obj.getMetadata().getName());
        }

        @Override
        public void onUpdate(Secret oldObj, Secret newObj) {
            logger.info("{} Secret updated", oldObj.getMetadata().getName());
        }

        @Override
        public void onDelete(Secret obj, boolean deletedFinalStateUnknown) {
            logger.info("{} Secret deleted", obj.getMetadata().getName());
        }
    }

}
