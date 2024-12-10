package org.babyfish.jimmer.sql.runtime;

import com.zyf.common.base.BaseEnum;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.babyfish.jimmer.impl.util.ClassCache;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.TypedProp;
import org.babyfish.jimmer.sql.Embeddable;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.MappedSuperclass;
import org.babyfish.jimmer.sql.ast.impl.TupleImplementor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Consumer;

public interface ScalarProvider<T, S> {

    @NotNull
    default Type getScalarType() {
        return Meta.of(this.getClass()).scalarType;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    default Class<S> getSqlType() {
        return (Class<S>) Meta.of(this.getClass()).sqlType;
    }

    T toScalar(@NotNull S sqlValue) throws Exception;

    S toSql(@NotNull T scalarValue) throws Exception;

    /**
     * User can override this method, it can return null, empty or handled property.
     * <ul>
     *     <li>Null or empty: Global scalar provider, can be applied to any properties</li>
     *     <li>Otherwise: Property-specific scalar provider</li>
     * </ul>
     *
     * <p>Actually, there are two ways to add property-specific scalar providers</p>
     * <ul>
     *     <li>Override {@link #getHandledProps()}</li>
     *     <li>Use {@link org.babyfish.jimmer.sql.JSqlClient.Builder#setScalarProvider(ImmutableProp, ScalarProvider)} or
     *     {@link org.babyfish.jimmer.sql.JSqlClient.Builder#setScalarProvider(TypedProp, ScalarProvider)}</li>
     * </ul>
     * @return Null or handled property.
     */
    default Collection<ImmutableProp> getHandledProps() {
        return null;
    }

    default boolean isJsonScalar() {
        return false;
    }

    default Reader<S> reader() {
        return null;
    }

    static <E extends Enum<E>> ScalarProvider<E, String> enumProviderByString(
            Class<E> enumType
    ) {
        return enumProviderByString(enumType, null);
    }

    static <E extends Enum<E>> ScalarProvider<E, String> enumProviderByString(
            Class<E> enumType,
            Consumer<EnumProviderBuilder<E, String>> block
    ) {
        EnumProviderBuilder<E, String> builder =
                EnumProviderBuilder.of(enumType, String.class, e -> {
                    if (e instanceof BaseEnum be) {
                        Object value = be.getValue();
                        if (value instanceof Number) {
                            throw new IllegalArgumentException(
                                    "这个枚举类" +
                                            enumType.getName() +
                                            " value 是数字类型，请加入 " +
                                            "@EnumType(EnumType.Strategy.ORDINAL) 注解！"
                            );
                        }
                        return value.toString();
                    }else{
                        return e.name();
                    }
                });
        if (block != null) {
            block.accept(builder);
        }
        return builder.build();
    }

    static <E extends Enum<E>> ScalarProvider<E, Integer> enumProviderByInt(
            Class<E> enumType
    ) {
        return enumProviderByInt(enumType, null);
    }

    static <E extends Enum<E>> ScalarProvider<E, Integer> enumProviderByInt(
            Class<E> enumType,
            Consumer<EnumProviderBuilder<E, Integer>> block
    ) {
        EnumProviderBuilder<E, Integer> builder =
                EnumProviderBuilder.of(enumType, Integer.class, e -> {
                    if (e instanceof BaseEnum be) {
                        Object value = be.getValue();
                        if (value instanceof Number v) {
                            return v.intValue();
                        }
                        return Integer.parseInt(value.toString());
                    }else{
                        return e.ordinal();
                    }
                });
        if (block != null) {
            block.accept(builder);
        }
        return builder.build();
    }

    static ScalarProvider<UUID, byte[]> uuidByByteArray() {
        return AbstractScalarProvider.UUID_BY_BYTE_ARRAY;
    }

    static ScalarProvider<UUID, String> uuidByString() {
        return AbstractScalarProvider.UUID_BY_STRING;
    }

    class Meta {

        private static final ClassCache<Meta> META_CACHE = new ClassCache<>(Meta::create);

        final Type scalarType;

        final Class<?> sqlType;

        private Meta(Type scalarType, Class<?> sqlType) {
            this.scalarType = scalarType;
            this.sqlType = sqlType;
        }

        public static Meta of(Class<?> scalarProviderType) {
            return META_CACHE.get(scalarProviderType);
        }

        static Meta create(Class<?> scalarProviderType) {
            if (!ScalarProvider.class.isAssignableFrom(scalarProviderType)) {
                throw new IllegalArgumentException(
                        "The `scalarProviderType` \"" +
                                scalarProviderType.getName() +
                                "\" + does not implement \"" +
                                ScalarProvider.class.getName() +
                                "\""
                );
            }
            Map<TypeVariable<?>, Type> argMap = TypeUtils.getTypeArguments(scalarProviderType, ScalarProvider.class);
            if (argMap.isEmpty()) {
                throw new IllegalStateException(
                        "Illegal type \"" +
                                scalarProviderType.getName() +
                                "\", it does not specify generic arguments for \"" +
                                ScalarProvider.class.getName() +
                                "\""
                );
            }
            TypeVariable<?>[] params = ScalarProvider.class.getTypeParameters();
            Type scalarType = argMap.get(params[0]);
            Class<?> sqlType = (Class<?>) argMap.get(params[1]);
            validateScalarType(scalarType);
            return new Meta(scalarType, sqlType);
        }

        static void validateScalarType(Type scalarType) {

            if (scalarType == UUID.class) {
                return; // UUID is standard type, but it can be overridden by ScalarProvider
            }

            if (!(scalarType instanceof Class<?>)) {
                return;
            }
            Class<?> scalarClass = (Class<?>) scalarType;

            if (scalarType == void.class) {
                throw new IllegalArgumentException(
                        "Illegal scalar type \"" +
                                scalarClass.getName() +
                                "\", it cannot be void"
                );
            }
            if (scalarType == Object.class) {
                throw new IllegalArgumentException(
                        "Illegal scalar type \"" +
                                scalarClass.getName() +
                                "\", scalar provider does not support object type which means any"
                );
            }
            if (TupleImplementor.class.isAssignableFrom(scalarClass)) {
                throw new IllegalArgumentException(
                        "Illegal scalar type \"" +
                                scalarClass.getName() +
                                "\", scalar provider does not support tuple type"
                );
            }
            if (ReaderManager.isStandardScalarType(scalarClass)) {
                throw new IllegalArgumentException(
                        "Illegal scalar type \"" +
                                ((Class<?>)scalarType).getName() +
                                "\", scalar provider does not support standard scalar type"
                );
            }
            Class<?> annotationType = getOrmAnnotationType(scalarClass);
            if (annotationType != null) {
                throw new IllegalArgumentException(
                        "Illegal scalar type \"" +
                                scalarClass.getName() +
                                "\", scalar provider does not support scalar type which is decorated by \"@" +
                                annotationType.getName() +
                                "\""
                );
            }
        }

        private static Class<?> getOrmAnnotationType(Class<?> type) {
            if (type == null) {
                return null;
            }
            if (type != Object.class) {
                if (type.isAnnotationPresent(Entity.class)) {
                    return Entity.class;
                }
                if (type.isAssignableFrom(MappedSuperclass.class)) {
                    return MappedSuperclass.class;
                }
                if (type.isAssignableFrom(Embeddable.class)) {
                    return Embeddable.class;
                }
            }
            Class<?> annoType = getOrmAnnotationType(type.getSuperclass());
            if (annoType != null) {
                return annoType;
            }
            for (Class<?> interfaceType : type.getInterfaces()) {
                annoType = getOrmAnnotationType(interfaceType);
                if (annoType != null) {
                    return annoType;
                }
            }
            return null;
        }
    }
}