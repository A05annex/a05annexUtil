* **version:** 0.8.5
* **status:** in development - do not use this yet
* **comments:** We are in the process of pulling this library out of a larger code base
  for release. Documentation and testing are incomplete, and while we are now using this
  library, we don't think it is ready for prime-time use.

# a05annexUtil
Utility classes for use in First FTC/FRC robot code, but that are not dependent on either
First FTC or First FRC infrastructure. We built this library as we realized that there 
is a lot of common code (functionality) that we use both inside and outside the
robot-specific code - for example, in autonomous path planning, reading/writing JSON
data files. We wanted to package that code so that it could be
used regardless of the FTC/FRC base platform libraries and/or hardware.

## Including a05annexUtil in your build.gradle

We wanted to package a05annexUtil so it was easy for us to use in future years, and
easy for you to use in your FRC projects. We found this was not as easy as we had
hoped. There are several paths for inclusion.

### Download the .jar and Make it Part of Your Project

This is simplest way to use **a05annexUtil**, and the only way we've found to include
it in our robot code. It follows the advice from this
[chiefdelphi post](https://www.chiefdelphi.com/t/adding-my-teams-library-as-a-vendor-library/339626)
and advises you:
* create a `libs` folder in your robot project
* copy the `util-0.8.5.jar` file from the 0.8.5 release into that directory
* in the dependencies section of the build.gradle file add the line:  
  `compile fileTree(dir: 'libs', include: ['*.jar'])`
  
### Modify build.gradle to Get the Dependency from the GitHub Packages Repository

If you are using **a05annexUtil** in another project, like our
[Swerve Path Planning](https://github.com/A05annex/SwervePathPlanning) project, you can make
these additions to your `build.gradle` plugins, repositories, and dependencies as
```
plugins {
    ...
    id 'maven'
}

repositories {
    ...
    maven {
        url "https://maven.pkg.github.com/A05annex/a05annexUtil"
        credentials {
            username = 'A05annex'
            password = 'c7a9e47cde81e97d2794331d73724a10806ba44a'
        }
    }

}

dependencies {
    compile 'org.a05annex:util:0.8.5'
    ...
}

```

### Get the a05annexutil from the Maven Repository

Stay tuned - we've not yet figured out the signing so we can make the release as part
of a github workflow.

## a05annexUtil Library Contents

* geo2d - some 2d geometric classes
  * KochanekBartelsSpline - a spline formulation continuous in position, velocity, and
    acceleration.
  * Vector2d - a 2d vector used in the spline code.
  * Plane2d - a 2d representation of a plane that we can use for collision testing between the robot
    and the field boundary or field elements during path planning.
* JsonSupport - a class with helper functions to aid in reading/writing JSON files.
* Utl - a class which extends the java `Math` class with variable argument min, max, and
  length functions.

    

