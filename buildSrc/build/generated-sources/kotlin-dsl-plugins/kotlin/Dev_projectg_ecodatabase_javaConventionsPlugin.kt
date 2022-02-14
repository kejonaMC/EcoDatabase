/**
 * Precompiled [dev.projectg.ecodatabase.java-conventions.gradle.kts][Dev_projectg_ecodatabase_java_conventions_gradle] script plugin.
 *
 * @see Dev_projectg_ecodatabase_java_conventions_gradle
 */
class Dev_projectg_ecodatabase_javaConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Dev_projectg_ecodatabase_java_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
