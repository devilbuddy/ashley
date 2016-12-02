package com.badlogic.ashley.tests;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by magnuso on 02/12/16.
 */
public class PooledEngineTest {

    private static class DummyComponent implements Component,  Pool.Poolable {

        public boolean flag = true;

        @Override
        public void reset() {
            flag = true;
        }

        @Override
        public String toString() {
            return "DummyComponent{" +
                    "flag=" + flag +
                    '}';
        }
    }

    private static PooledEngine pooledEngine = new PooledEngine();
    private static Family dummyFamily = Family.all(DummyComponent.class).get();
    private static ComponentMapper<DummyComponent> dummyComponentComponentMapper = ComponentMapper.getFor(DummyComponent.class);


    private static class TestSystem extends EntitySystem {
        @Override
        public void update (float deltaTime) {
            ImmutableArray<Entity> entities = pooledEngine.getEntitiesFor(dummyFamily);
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                DummyComponent dummyComponent = dummyComponentComponentMapper.get(entity);
                dummyComponent.flag = false; // Setting flag to false here
                pooledEngine.removeEntity(entity);
            }

            Entity secondEntity = pooledEngine.createEntity();
            DummyComponent secondComponent = pooledEngine.createComponent(DummyComponent.class);
            secondComponent.flag = true; // Set the flag to true
            secondEntity.add(secondComponent);
            pooledEngine.addEntity(secondEntity);

            Entity e = pooledEngine.getEntitiesFor(dummyFamily).get(0);
            DummyComponent c = dummyComponentComponentMapper.get(e);

            // This is what does not make sense to me.. "flag" should be true.. or?
            if (c.flag == false) {
                throw new RuntimeException("I'd expect this to be true");
            }
        }
    }


    public static void main (String[] args) {
        pooledEngine.addSystem(new TestSystem());

        Entity firstEntity = pooledEngine.createEntity();
        DummyComponent firstComponent = pooledEngine.createComponent(DummyComponent.class);
        firstEntity.add(firstComponent);
        pooledEngine.addEntity(firstEntity);


        pooledEngine.update(0.0f);


    }

}
