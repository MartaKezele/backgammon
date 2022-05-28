package mk.bg.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import mk.bg.constants.HtmlConstants;

/**
 *
 * @author Marta
 */
public class ReflectionUtils {

    // private members
    private static final String PACKAGE = "Package:";
    private static final String MODIFIERS = "Modifiers:";
    private static final String EXTENDED_SUPERCLASSES
            = "Extended superclasses:";
    private static final String IMPLEMENTED_INTERFACES
            = "Implemented interfaces:";
    private static final String CONSTRUCTORS = "Constructors:";
    private static final String FIELDS = "Fields:";
    private static final String METHODS = "Methods:";
    private static final String THROWS = "throws";

    // private contructors
    private ReflectionUtils() {

    }

    // public methods
    public static void readClassInfo(Class<?> clazz, StringBuilder classInfo) {
        appendPackage(clazz, classInfo);
        appendModifiers(clazz, classInfo);
        appendSuperclasses(clazz, classInfo, true);
        appendInterfaces(clazz, classInfo);
    }

    private static void appendPackage(Class<?> clazz, StringBuilder classInfo) {
        classInfo
                .append(HtmlConstants.H3_OPEN_TAG)
                .append(PACKAGE)
                .append(HtmlConstants.H3_CLOSE_TAG)
                .append(clazz.getPackage());
    }

    private static void appendModifiers(Class<?> clazz,
            StringBuilder classInfo) {

        classInfo
                .append(HtmlConstants.H3_OPEN_TAG)
                .append(MODIFIERS)
                .append(HtmlConstants.H3_CLOSE_TAG)
                .append(Modifier.toString(clazz.getModifiers()));
    }

    private static void appendSuperclasses(Class<?> clazz,
            StringBuilder classInfo, boolean first) {
        Class<?> superclass = clazz.getSuperclass();
        if (superclass == null) {
            return;
        }
        if (first) {
            classInfo.append(HtmlConstants.H3_OPEN_TAG)
                    .append(EXTENDED_SUPERCLASSES)
                    .append(HtmlConstants.H3_CLOSE_TAG);
        }
        classInfo
                .append(" ")
                .append(superclass.getName())
                .append(HtmlConstants.BR);
        appendSuperclasses(superclass, classInfo, false);
    }

    private static void appendInterfaces(Class<?> clazz,
            StringBuilder classInfo) {
        if (clazz.getInterfaces().length > 0) {
            classInfo.append(HtmlConstants.H3_OPEN_TAG)
                    .append(IMPLEMENTED_INTERFACES)
                    .append(HtmlConstants.H3_CLOSE_TAG);
        }
        for (Class<?> in : clazz.getInterfaces()) {
            classInfo
                    .append(in.getName())
                    .append(HtmlConstants.BR);
        }
    }

    public static void readClassAndMembersInfo(Class<?> clazz,
            StringBuilder classAndMembersInfo) {
        readClassInfo(clazz, classAndMembersInfo);
        appendFields(clazz, classAndMembersInfo);
        appendConstructors(clazz, classAndMembersInfo);
        appendMethods(clazz, classAndMembersInfo);
    }

    private static void appendFields(Class<?> clazz,
            StringBuilder classAndMembersInfo) {
        //Field[] fields = clazz.getFields(); // returns public and inherited
        Field[] fields = clazz.getDeclaredFields();
        // returns public, protected, default (package) access, 
        // and private fields, but excludes inherited fields
        if (fields.length > 0) {
            classAndMembersInfo.append(HtmlConstants.H3_OPEN_TAG)
                    .append(FIELDS)
                    .append(HtmlConstants.H3_CLOSE_TAG);
        }
        classAndMembersInfo
                .append(HtmlConstants.OL_OPEN_TAG);
        for (Field field : fields) {
            classAndMembersInfo
                    .append(HtmlConstants.LI_OPEN_TAG)
                    .append(field)
                    .append(HtmlConstants.LI_CLOSE_TAG);
        }
        classAndMembersInfo
                .append(HtmlConstants.OL_CLOSE_TAG);
    }

    private static void appendMethods(Class<?> clazz,
            StringBuilder classAndMembersInfo) {
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length > 0) {
            classAndMembersInfo.append(HtmlConstants.H3_OPEN_TAG)
                    .append(METHODS)
                    .append(HtmlConstants.H3_CLOSE_TAG);
        }
        classAndMembersInfo
                .append(HtmlConstants.OL_OPEN_TAG);
        for (Method method : methods) {
            appendMethodAnnotations(method, classAndMembersInfo);
            classAndMembersInfo
                    .append(HtmlConstants.LI_OPEN_TAG)
                    .append(Modifier.toString(method.getModifiers()))
                    .append(" ")
                    .append(method.getReturnType())
                    .append(" ")
                    .append(method.getName());
            appendParameters(method, classAndMembersInfo);
            appendExceptions(method, classAndMembersInfo);
            classAndMembersInfo.append(HtmlConstants.LI_CLOSE_TAG);
        }
        classAndMembersInfo
                .append(HtmlConstants.OL_CLOSE_TAG);
    }

    private static void appendMethodAnnotations(Executable executable,
            StringBuilder classAndMembersInfo) {
        for (Annotation annotation : executable.getAnnotations()) {
            classAndMembersInfo
                    .append(annotation)
                    .append(HtmlConstants.BR);
        }
    }

    private static void appendParameters(Executable executable,
            StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append("(");
        for (Parameter parameter : executable.getParameters()) {
            classAndMembersInfo
                    .append(parameter)
                    .append(", ");
        }
        if (classAndMembersInfo.toString().endsWith(", ")) {
            classAndMembersInfo.delete(classAndMembersInfo.length() - 2,
                    classAndMembersInfo.length());
        }
        classAndMembersInfo.append(")");
    }

    private static void appendExceptions(Executable executable,
            StringBuilder classAndMembersInfo) {
        Class<?>[] exceptionTypes = executable.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            classAndMembersInfo.append(" ")
                    .append(THROWS)
                    .append(" ");
            for (Class<?> exceptionType : exceptionTypes) {
                classAndMembersInfo
                        .append(exceptionType)
                        .append(", ");
            }
            if (classAndMembersInfo.toString().endsWith(", ")) {
                classAndMembersInfo.delete(classAndMembersInfo.length() - 2,
                        classAndMembersInfo.length());
            }
        }
    }

    private static void appendConstructors(Class<?> clazz,
            StringBuilder classAndMembersInfo) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 0) {
            classAndMembersInfo.append(HtmlConstants.H3_OPEN_TAG)
                    .append(CONSTRUCTORS)
                    .append(HtmlConstants.H3_CLOSE_TAG);
        }
        classAndMembersInfo
                .append(HtmlConstants.OL_OPEN_TAG);
        for (Constructor constructor : constructors) {
            appendMethodAnnotations(constructor, classAndMembersInfo);
            classAndMembersInfo
                    .append(HtmlConstants.LI_OPEN_TAG)
                    .append(Modifier.toString(constructor.getModifiers()))
                    .append(" ")
                    .append(constructor.getName());
            appendParameters(constructor, classAndMembersInfo);
            appendExceptions(constructor, classAndMembersInfo);
            classAndMembersInfo.append(HtmlConstants.LI_CLOSE_TAG);
        }
        classAndMembersInfo
                .append(HtmlConstants.OL_CLOSE_TAG);
    }
}
