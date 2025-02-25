package frc.robot.subsystems.swerve;

import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.lib.util.flippable.FlippableRotation2d;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import static frc.robot.RobotContainer.POSE_ESTIMATOR;
import static frc.robot.RobotContainer.SWERVE;
import static frc.robot.subsystems.swerve.SwerveConstants.*;
import static frc.robot.subsystems.swerve.SwerveModuleConstants.MODULES;
import static frc.robot.utilities.PathPlannerConstants.PATHPLANNER_CONSTRAINTS;

public class SwerveCommands {
    public static Command stopDriving() {
        return new InstantCommand(SWERVE::stop);
    }

    public static Command lockSwerve() {
        return Commands.run(
                () -> {
                    final SwerveModuleState
                            right = new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
                            left = new SwerveModuleState(0, Rotation2d.fromDegrees(45));

                    MODULES[0].setTargetState(left, false);
                    MODULES[1].setTargetState(right, false);
                    MODULES[2].setTargetState(right, false);
                    MODULES[3].setTargetState(left, false);
                },
                SWERVE
        );
    }

    public static Command goToPoseBezier(Pose2d targetPose) {
        return AutoBuilder.pathfindToPose(targetPose, PATHPLANNER_CONSTRAINTS);
    }

    public static Command goToPosePID(Pose2d targetPose) {
        return new FunctionalCommand(
                () -> {
                    SWERVE.resetRotationController();
                    SWERVE.setGoalRotationController(targetPose.getRotation());
                    },
                () -> SWERVE.driveToPose(targetPose),
                interrupt -> SWERVE.stop(),
                () -> SWERVE.isAtPose(targetPose, 0.01, 0.3),
                SWERVE
        );
    }

    public static Command goToPoseTrapezoidal(Pose2d targetPose, double allowedDistanceFromTargetMeters, double allowedRotationalErrorDegrees) {
        return new FunctionalCommand(
                () -> {
                    SWERVE.resetRotationController();
                    SWERVE.resetTranslationalControllers();

                    SWERVE.setGoalRotationController(targetPose.getRotation());
                    SWERVE.setGoalTranslationalControllers(targetPose);
                },
                () -> SWERVE.driveToPoseTrapezoidal(targetPose),
                interrupt -> SWERVE.stop(),
                () -> SWERVE.isAtPose(targetPose, allowedDistanceFromTargetMeters, allowedRotationalErrorDegrees),
                SWERVE
        );
    }

    public static Command resetGyro() {
        return Commands.runOnce(() -> SWERVE.setGyroHeading(Rotation2d.fromDegrees(0)), SWERVE);
    }

    public static Command driveOpenLoop(DoubleSupplier x, DoubleSupplier y, DoubleSupplier rotation, BooleanSupplier robotCentric) {
        return Commands.run(
                () -> SWERVE.driveOpenLoop(x.getAsDouble(), y.getAsDouble(), rotation.getAsDouble(), robotCentric.getAsBoolean()),
                SWERVE
        );
    }

    public static Command driveWhilstRotatingToTarget(DoubleSupplier x, DoubleSupplier y, Pose2d target, BooleanSupplier robotCentric) {
        return new FunctionalCommand(
                () -> {
                    SWERVE.resetRotationController();
                    SWERVE.setGoalRotationController(target.getRotation());
                },
                () -> SWERVE.driveWithTarget(x.getAsDouble(), y.getAsDouble(), robotCentric.getAsBoolean()),
                interrupt -> {},
                () -> false,
                SWERVE
        );
    }

    public static Command rotateToTarget(Pose2d target) {
        return new FunctionalCommand(
                () -> {
                    SWERVE.resetRotationController();
                    SWERVE.setGoalRotationController(target.getRotation());
                },
                () -> SWERVE.driveWithTarget(0, 0, false),
                interrupt -> {},
                SWERVE_ROTATION_CONTROLLER::atGoal,
                SWERVE
        );
    }

    public static Command rotateToTarget(FlippableRotation2d rotationTraget) {
        return new FunctionalCommand(
                () -> {
                    SWERVE_ROTATIONAL_CONTROLLER_ACCURATE.reset(POSE_ESTIMATOR.getCurrentPose().getRotation().getDegrees());
                    SWERVE_ROTATIONAL_CONTROLLER_ACCURATE.setGoal(new TrapezoidProfile.State(rotationTraget.get().getDegrees(), 0));
                },
                () -> {
                    SWERVE.rotateToTargetAccurate();
                },
                interrupt -> {},
                SWERVE_ROTATIONAL_CONTROLLER_ACCURATE::atGoal,
                SWERVE
        );
    }

    public static Command rotateToTarget(Rotation2d rotationTraget) {
        return new FunctionalCommand(
                () -> {
                    SWERVE.resetRotationController();
                    SWERVE.setGoalRotationController(rotationTraget);
                },
                () -> SWERVE.driveWithTarget(0, 0, false),
                interrupt -> {},
                SWERVE_ROTATION_CONTROLLER::atGoal,
                SWERVE
        );
    }
}
