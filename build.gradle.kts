import nl.javadude.gradle.plugins.license.DownloadLicensesExtension.license
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
    id("com.github.hierynomus.license")
    id("org.owasp.dependencycheck")
    id("com.github.sgtsilvio.gradle.utf8")
    id("com.github.sgtsilvio.gradle.metadata")
    id("com.github.sgtsilvio.gradle.javadoc-links")

    /* Publishing Plugins */
    id("io.github.gradle-nexus.publish-plugin")
    id("signing")

    /* Code Quality Plugins */
    id("jacoco")
    id("pmd")
    id("com.github.spotbugs")
    id("de.thetaphi.forbiddenapis")
}


/* ******************** metadata ******************** */

group = "com.hivemq"
description = "HiveMQ CE is a Java-based open source MQTT broker that fully supports MQTT 3.x and MQTT 5"

metadata {
    readableName.set("HiveMQ Community Edition")
    organization {
        name.set("HiveMQ GmbH")
        url.set("https://www.hivemq.com/")
    }
    license {
        apache2()
    }
    developers {
        developer {
            id.set("cschaebe")
            name.set("Christoph Schaebel")
            email.set("christoph.schaebel@hivemq.com")
        }
        developer {
            id.set("lbrandl")
            name.set("Lukas Brandl")
            email.set("lukas.brandl@hivemq.com")
        }
        developer {
            id.set("flimpoeck")
            name.set("Florian Limpoeck")
            email.set("florian.limpoeck@hivemq.com")
        }
        developer {
            id.set("sauroter")
            name.set("Georg Held")
            email.set("georg.held@hivemq.com")
        }
        developer {
            id.set("SgtSilvio")
            name.set("Silvio Giebl")
            email.set("silvio.giebl@hivemq.com")
        }
    }
    github {
        org.set("hivemq")
        repo.set("hivemq-community-edition")
        issues()
    }
}


/* ******************** java ******************** */

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }

    withJavadocJar()
    withSourcesJar()
}


/* ******************** dependencies ******************** */

repositories {
    mavenCentral()
}

dependencies {
    api("com.hivemq:hivemq-extension-sdk:${property("hivemq-extension-sdk.version")}")

    /* netty */
    implementation("io.netty:netty-buffer:${property("netty.version")}")
    implementation("io.netty:netty-codec:${property("netty.version")}")
    implementation("io.netty:netty-codec-http:${property("netty.version")}")
    implementation("io.netty:netty-common:${property("netty.version")}")
    implementation("io.netty:netty-handler:${property("netty.version")}")
    implementation("io.netty:netty-transport:${property("netty.version")}")

    /* logging */
    implementation("org.slf4j:slf4j-api:${property("slf4j.version")}")
    implementation("org.slf4j:jul-to-slf4j:${property("slf4j.version")}")
    implementation("ch.qos.logback:logback-classic:${property("logback.version")}")

    /* security - bouncycastle */
    implementation("org.bouncycastle:bcprov-jdk15on:${property("bouncycastle.version")}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${property("bouncycastle.version")}")

    /* persistence - rocksdb */
    implementation("org.rocksdb:rocksdbjni:${property("rocksdb.version")}")

    /* persistence - xodus */
    implementation("org.jetbrains.xodus:xodus-openAPI:${property("xodus.version")}") {
        exclude("org.jetbrains", "annotations")
    }
    implementation("org.jetbrains.xodus:xodus-environment:${property("xodus.version")}") {
        exclude("org.jetbrains", "annotations")
    }

    /* config - xml */
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:${property("jaxb.version")}")
    runtimeOnly("com.sun.xml.bind:jaxb-impl:${property("jaxb.version")}")

    /* metrics */
    api("io.dropwizard.metrics:metrics-core:${property("metrics.version")}")
    implementation("io.dropwizard.metrics:metrics-jmx:${property("metrics.version")}")
    runtimeOnly("io.dropwizard.metrics:metrics-logback:${property("metrics.version")}")

    /* metrics - oshi */
    implementation("com.github.oshi:oshi-core:${property("oshi.version")}")
    // net.java.dev.jna:jna (transitive dependency of com.github.oshi:oshi-core) is used in imports

    /* dependency injection */
    implementation("com.google.inject:guice:${property("guice.version")}") {
        exclude("com.google.guava", "guava")
    }
    implementation("javax.annotation:javax.annotation-api:${property("javax.annotation.version")}")
    // javax.inject:javax.inject (transitive dependency of com.google.inject:guice) is used in imports

    /* common - apache */
    implementation("commons-io:commons-io:${property("commons-io.version")}")
    implementation("org.apache.commons:commons-lang3:${property("commons-lang.version")}")

    /* common - guava */
    implementation("com.google.guava:guava:${property("guava.version")}") {
        exclude("org.checkerframework", "checker-qual")
        exclude("com.google.errorprone", "error_prone_annotations")
        exclude("org.codehaus.mojo", "animal-sniffer-annotations")
    }
    // com.google.code.findbugs:jsr305 (transitive dependency of com.google.guava:guava) is used in imports

    /* hashing */
    implementation("net.openhft:zero-allocation-hashing:${property("zero-allocation-hashing.version")}")

    /* jackson */
    implementation("com.fasterxml.jackson.core:jackson-databind:${property("jackson.version")}")

    /* JCTools*/
    implementation("org.jctools:jctools-core:${property("jctools.version")}")


    /* temporary dependencies to override transitive ones that have security vulnerabilities */
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:${property("kotlin.version")}")
        implementation("org.apache.commons:commons-compress:${property("commons-compress.version")}")
    }

    testImplementation("junit:junit:${property("junit.version")}")
    testImplementation("org.mockito:mockito-core:${property("mockito.version")}")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:${property("equalsverifier.version")}")
    testImplementation("net.jodah:concurrentunit:${property("concurrentunit.version")}")
    testImplementation("org.jboss.shrinkwrap:shrinkwrap-api:${property("shrinkwrap.version")}")
    testImplementation("net.bytebuddy:byte-buddy:${property("bytebuddy.version")}")
    testImplementation("com.github.tomakehurst:wiremock-standalone:${property("wiremock.version")}")
    testImplementation("com.github.stefanbirkner:system-rules:${property("system-rules.version")}") {
        exclude("junit", "junit-dep")
    }

    testRuntimeOnly("org.jboss.shrinkwrap:shrinkwrap-impl-base:${property("shrinkwrap.version")}")

    //plugins for spotbugs
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.8.0")
}


/* ******************** test ******************** */

tasks.test {
    jvmArgs(
        "-Dfile.encoding=UTF-8",
        "--add-opens",
        "java.base/java.lang=ALL-UNNAMED",
        "--add-opens",
        "java.base/java.nio=ALL-UNNAMED",
        "--add-opens",
        "java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-opens",
        "jdk.management/com.sun.management.internal=ALL-UNNAMED",
        "--add-exports",
        "java.base/jdk.internal.misc=ALL-UNNAMED"
    )
    minHeapSize = "128m"
    maxHeapSize = "2048m"

    val inclusions = rootDir.resolve("inclusions.txt")
    val exclusions = rootDir.resolve("exclusions.txt")
    if (inclusions.exists()) {
        include(inclusions.readLines())
    } else if (exclusions.exists()) {
        exclude(exclusions.readLines())
    }

    testLogging {
        events = setOf(TestLogEvent.STARTED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }

    // use the same tmpdir as the runner
    val tempDir = System.getProperty("java.io.tmpdir")
    if (tempDir != null) {
        jvmArgs("-Djava.io.tmpdir=$tempDir")
    }
}


/* ******************** packaging ******************** */

tasks.jar {
    manifest.attributes(
        "Implementation-Title" to "HiveMQ",
        "Implementation-Vendor" to metadata.organization!!.name.get(),
        "Implementation-Version" to project.version,
        "HiveMQ-Version" to project.version,
        "Main-Class" to "com.hivemq.HiveMQServer"
    )
}

tasks.shadowJar {
    mergeServiceFiles()
}

tasks.register<Zip>("packaging") {
    group = "packaging"

    val name = "hivemq-ce-${project.version}"

    destinationDirectory.set(buildDir.resolve("zip"))
    archiveFileName.set("$name.zip")

    from("$projectDir/src/packaging") { exclude("**/.gitkeep") }
    from("$projectDir/src/main/resources/config.xml") { into("conf") }
    from(tasks.shadowJar) { into("bin").rename { "hivemq.jar" } }
    into(name)
}

defaultTasks("clean", "packaging")

tasks.javadoc {
    (options as StandardJavadocDocletOptions).addStringOption("-html5")

    include("com/hivemq/embedded/*")

    doLast {
        javaexec {
            main = "-jar"
            args("$projectDir/gradle/tools/javadoc-cleaner-1.0.jar")
        }
    }

    doLast { // javadoc search fix for jdk 11 https://bugs.openjdk.java.net/browse/JDK-8215291
        copy {
            from("$destinationDir/search.js")
            into(temporaryDir)
            filter { line -> line.replace("if (ui.item.p == item.l) {", "if (item.m && ui.item.p == item.l) {") }
        }
        delete("$destinationDir/search.js")
        copy {
            from("$temporaryDir/search.js")
            into(destinationDir!!)
        }
    }
}


/* ******************** checks ******************** */

jacoco {
    toolVersion = "${property("jacoco.version")}"
}

pmd {
    toolVersion = "${property("pmd.version")}"
    sourceSets = listOf(project.sourceSets.main.get())
    isIgnoreFailures = true
    rulesMinimumPriority.set(3)
}

spotbugs {
    toolVersion.set("${property("spotbugs.version")}")
    ignoreFailures.set(true)
    reportsDir.fileValue(file("${buildDir}/reports/findbugs"))
    reportLevel.set(com.github.spotbugs.snom.Confidence.MEDIUM)
}

dependencyCheck {
    analyzers.apply {
        centralEnabled = false
    }
    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL
    scanConfigurations = listOf("runtimeClasspath")
    suppressionFile = "${projectDir}/gradle/dependency-check/suppress.xml"
}

tasks.check {
    dependsOn(tasks.dependencyCheckAnalyze)
}

forbiddenApis {
    bundledSignatures = setOf("jdk-system-out")
}

tasks.forbiddenApisMain {
    exclude("**/BatchedException.class")
    exclude("**/LoggingBootstrap.class")
}

tasks.forbiddenApisTest {
    enabled = false
}


/* ******************** compliance ******************** */

license {
    header = file("$projectDir/HEADER")
    mapping("java", "SLASHSTAR_STYLE")
}

downloadLicenses {
    val apache_2 = license("Apache License, Version 2.0", "https://opensource.org/licenses/Apache-2.0")
    val mit = license("MIT License", "https://opensource.org/licenses/MIT")
    val cddl_1_0 = license("CDDL, Version 1.0", "https://opensource.org/licenses/CDDL-1.0")
    val cddl_1_1 = license("CDDL, Version 1.1", "https://oss.oracle.com/licenses/CDDL+GPL-1.1")
    val lgpl_2_0 = license("LGPL, Version 2.0", "https://opensource.org/licenses/LGPL-2.0")
    val lgpl_2_1 = license("LGPL, Version 2.1", "https://opensource.org/licenses/LGPL-2.1")
    val lgpl_3_0 = license("LGPL, Version 3.0", "https://opensource.org/licenses/LGPL-3.0")
    val epl_1_0 = license("EPL, Version 1.0", "https://opensource.org/licenses/EPL-1.0")
    val epl_2_0 = license("EPL, Version 2.0", "https://opensource.org/licenses/EPL-2.0")
    val edl_1_0 = license("EDL, Version 1.0", "https://www.eclipse.org/org/documents/edl-v10.php")
    val bsd_3clause = license("BSD 3-Clause License", "https://opensource.org/licenses/BSD-3-Clause")
    val bouncycastle = license("Bouncy Castle License", "https://www.bouncycastle.org/licence.html")
    val w3c = license("W3C License", "https://opensource.org/licenses/W3C")
    val cc0 = license("CC0", "https://creativecommons.org/publicdomain/zero/1.0/")

    aliases = mapOf(
        apache_2 to listOf(
            "Apache 2",
            "Apache 2.0",
            "Apache License 2.0",
            "Apache License, 2.0",
            "Apache License v2.0",
            "Apache License, Version 2",
            "Apache License Version 2.0",
            "Apache License, Version 2.0",
            "Apache License, version 2.0",
            "The Apache License, Version 2.0",
            "Apache Software License - Version 2.0",
            "Apache Software License, version 2.0",
            "The Apache Software License, Version 2.0"
        ),
        mit to listOf(
            "MIT License",
            "MIT license",
            "The MIT License",
            "The MIT License (MIT)"
        ),
        cddl_1_0 to listOf(
            "CDDL, Version 1.0",
            "Common Development and Distribution License 1.0",
            "COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0",
            license("CDDL", "https://glassfish.dev.java.net/public/CDDLv1.0.html")
        ),
        cddl_1_1 to listOf(
            "CDDL 1.1",
            "CDDL, Version 1.1",
            "Common Development And Distribution License 1.1",
            "CDDL+GPL License",
            "CDDL + GPLv2 with classpath exception",
            "Dual license consisting of the CDDL v1.1 and GPL v2",
            "CDDL or GPLv2 with exceptions",
            "CDDL/GPLv2+CE"
        ),
        lgpl_2_0 to listOf(
            "LGPL, Version 2.0",
            "GNU General Public License, version 2"
        ),
        lgpl_2_1 to listOf(
            "LGPL, Version 2.1",
            "LGPL, version 2.1",
            "GNU Lesser General Public License version 2.1 (LGPLv2.1)",
            license("GNU Lesser General Public License", "http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html")
        ),
        lgpl_3_0 to listOf(
            "LGPL, Version 3.0",
            "Lesser General Public License, version 3 or greater"
        ),
        epl_1_0 to listOf(
            "EPL, Version 1.0",
            "Eclipse Public License - v 1.0",
            "Eclipse Public License - Version 1.0",
            license("Eclipse Public License", "http://www.eclipse.org/legal/epl-v10.html")
        ),
        epl_2_0 to listOf(
            "EPL 2.0",
            "EPL, Version 2.0"
        ),
        edl_1_0 to listOf(
            "EDL 1.0",
            "EDL, Version 1.0",
            "Eclipse Distribution License - v 1.0"
        ),
        bsd_3clause to listOf(
            "BSD 3-clause",
            "BSD-3-Clause",
            "BSD 3-Clause License",
            "3-Clause BSD License",
            "New BSD License",
            license("BSD", "http://asm.ow2.org/license.html"),
            license("BSD", "http://asm.objectweb.org/license.html"),
            license("BSD", "LICENSE.txt")
        ),
        bouncycastle to listOf("Bouncy Castle Licence"),
        w3c to listOf(
            "W3C License",
            "W3C Software Copyright Notice and License",
            "The W3C Software License"
        ),
        cc0 to listOf(
            "CC0",
            "Public Domain"
        )
    )

    dependencyConfiguration = "runtimeClasspath"
    excludeDependencies = listOf("com.hivemq:hivemq-extension-sdk:${property("hivemq-extension-sdk.version")}")
}

tasks.register("updateThirdPartyLicenses") {
    group = "license"
    dependsOn(tasks.downloadLicenses)
    doLast {
        javaexec {
            main = "-jar"
            args(
                "$projectDir/gradle/tools/license-third-party-tool-2.0.jar",
                "$buildDir/reports/license/dependency-license.xml",
                "$projectDir/src/packaging/third-party-licenses/licenses",
                "$projectDir/src/packaging/third-party-licenses/licenses.html"
            )
        }
    }
}


/* ******************** publishing ******************** */

publishing {
    publications {
        register<MavenPublication>("distribution") {
            artifact(tasks.named("packaging"))

            artifactId = "hivemq-community-edition"
        }

        register<MavenPublication>("embedded") {
            from(components["java"])

            artifactId = "hivemq-community-edition-embedded"
        }
    }
}

signing {
    val signKey: String? by project
    val signKeyPass: String? by project
    useInMemoryPgpKeys(signKey, signKeyPass)
    sign(publishing.publications["embedded"])
}

nexusPublishing {
    repositories {
        sonatype()
    }
}
