/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.app;

import java.util.Calendar;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import static javafx.application.Application.launch;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathBuilder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.RotateBuilder;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.w3c.dom.events.MouseEvent;

/**
 *
 * @author attila
 */
public class AnalogClock extends Group {

    private static final double START_RADIUS = 100;
    private static final int NO_HOUR_TICKS = 12;
    private static final int NO_MINUTE_TICKS = 60;
    private final AnalogClockwork clockwork = new AnalogClockwork();

    public AnalogClock() {
        getChildren().addAll( clockDial(),
                        minuteTickMarks(),
                        hourTickMarks(),
                        hourHand(),
                        minuteHand(),
                        secondsHand());
    }

    

    private Node clockDial() {

        Stop stops[] = {
            new Stop(0.92, Color.WHITE),
            new Stop(0.98, Color.BLACK),
            new Stop(1.0, Color.BLACK)
        };
        RadialGradient gradient = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, stops);

        Circle circle = new Circle(START_RADIUS, gradient);
        circle.setCenterX(START_RADIUS);
        circle.setCenterY(START_RADIUS);
        return circle;
    }

    private Node hourHand() {
        double distanceFromRim = START_RADIUS * 0.5;
        Rotate rotate = handRotation(clockwork.hourHandAngle());
        return hourOrMinuteHand(distanceFromRim, Color.BLACK, rotate);
    }

    private Node minuteHand() {
        double distanceFromRim = START_RADIUS * 0.75;
        Rotate rotate = handRotation(clockwork.minuteHandAngle());
        return hourOrMinuteHand(distanceFromRim, Color.BLACK, rotate);
    }

    private Node secondsHand() {
        double distanceFromRim = START_RADIUS * 0.7;
        Color handColor = Color.RED;
        Rotate rotate = handRotation(clockwork.secondsHandAngle());
        return GroupBuilder.create()
                .children(
                        secondsHandLine(distanceFromRim, handColor),
                        secondsHandTip(distanceFromRim, handColor),
                        centerPoint(handColor)
                )
                .transforms(rotate)
                .build();
    }

    private Node secondsHandTip(double distanceFromRim, Color handColor) {
        double handTipRadius = START_RADIUS * 0.07;
        return CircleBuilder.create()
                .centerX(START_RADIUS)
                .centerY(START_RADIUS - distanceFromRim)
                .fill(handColor)
                .radius(handTipRadius)
                .build();
    }

    private Node secondsHandLine(double distanceFromRim, Color handColor) {
        double handCenterExtension = START_RADIUS * 0.15;
        double handWidth = START_RADIUS * 0.02;
        return LineBuilder.create()
                .startX(START_RADIUS)
                .startY(START_RADIUS - distanceFromRim)
                .endX(START_RADIUS)
                .endY(START_RADIUS + handCenterExtension)
                .strokeWidth(handWidth)
                .stroke(handColor)
                .build();
    }

    private Rotate handRotation(ObservableDoubleValue handAngle) {
        Rotate handRotation = RotateBuilder.create()
                .pivotX(START_RADIUS)
                .pivotY(START_RADIUS)
                .build();
        handRotation.angleProperty().bind(handAngle);
        return handRotation;
    }

    private Node hourOrMinuteHand(double distanceFromRim, Color color, Rotate rotate) {
        double handBaseWidth = START_RADIUS * 0.05;
        double handTipWidth = START_RADIUS * 0.03;
        double handCenterExtension = START_RADIUS * 0.15;
        double leftBaseCornerX = START_RADIUS - handBaseWidth;
        double baseY = START_RADIUS + handCenterExtension;
        double tipY = START_RADIUS - distanceFromRim;
        double leftTipCornerX = START_RADIUS - handTipWidth;
        double rightTipCornerX = START_RADIUS + handTipWidth;
        double rightCornerBaseX = START_RADIUS + handBaseWidth;
        return PathBuilder.create()
                .fill(color)
                .stroke(Color.TRANSPARENT)
                .elements(
                        new MoveTo(leftBaseCornerX, baseY),
                        new LineTo(leftTipCornerX, tipY),
                        new LineTo(rightTipCornerX, tipY),
                        new LineTo(rightCornerBaseX, baseY),
                        new LineTo(leftBaseCornerX, baseY)
                )
                .transforms(rotate)
                .build();
    }

    private Node minuteTickMarks() {
        Group tickMarkGroup = new Group();
        int noTicks = NO_MINUTE_TICKS;
        for (int n = 0; n < noTicks; n++) {
            tickMarkGroup.getChildren().add(tickMark(n, 1, noTicks));
        }
        return tickMarkGroup;
    }

    private Node hourTickMarks() {
        Group tickMarkGroup = new Group();
        int noTicks = NO_HOUR_TICKS;
        for (int n = 0; n < noTicks; n++) {
            tickMarkGroup.getChildren().add(tickMark(n, 6, noTicks));
        }
        return tickMarkGroup;
    }

    private Node tickMark(int n, double width, int noTicks) {
        return LineBuilder.create()
                .startX(START_RADIUS)
                .startY(START_RADIUS * 0.12)
                .endX(START_RADIUS)
                .endY(START_RADIUS * 0.2 + width * 2)
                .transforms(
                        RotateBuilder.create()
                        .pivotX(START_RADIUS)
                        .pivotY(START_RADIUS)
                        .angle(360 / noTicks * n)
                        .build()
                )
                .strokeWidth(width)
                .build();
    }

    private Node centerPoint(Color color) {
        return CircleBuilder.create()
                .fill(color)
                .radius(0.03 * START_RADIUS)
                .centerX(START_RADIUS)
                .centerY(START_RADIUS)
                .build();
    }

   /* private void setUpMouseForScaleAndMove(final Stage stage, final Parent root) {
        SimpleDoubleProperty mouseStartX = new SimpleDoubleProperty(0);
        SimpleDoubleProperty mouseStartY = new SimpleDoubleProperty(0);
        root.setOnMousePressed(setMouseStartPoint(mouseStartX, mouseStartY));
        root.setOnMouseDragged(moveWhenDragging(stage, mouseStartX, mouseStartY));
        root.onScrollProperty().set(scaleWhenScrolling(stage, root));
    }

    private EventHandler<? super MouseEvent> setMouseStartPoint(final SimpleDoubleProperty mouseStartX, final SimpleDoubleProperty mouseStartY) {
        return new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                mouseStartX.set(mouseEvent.getX());
                mouseStartY.set(mouseEvent.getY());
            }
        };
    }

    private EventHandler<MouseEvent> moveWhenDragging(final Stage stage, final SimpleDoubleProperty mouseStartX, final SimpleDoubleProperty mouseStartY) {
        return new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                stage.setX(stage.getX() + mouseEvent.getX() - mouseStartX.doubleValue());
                stage.setY(stage.getY() + mouseEvent.getY() - mouseStartY.doubleValue());
            }
        };
    }

    private EventHandler<ScrollEvent> scaleWhenScrolling(final Stage stage, final Parent root) {
        return new EventHandler<ScrollEvent>() {
            public void handle(ScrollEvent scrollEvent) {
                double scroll = scrollEvent.getDeltaY();
                root.setScaleX(root.getScaleX() + scroll / 100);
                root.setScaleY(root.getScaleY() + scroll / 100);
                root.setTranslateX(root.getTranslateX() + scroll);
                root.setTranslateY(root.getTranslateY() + scroll);
                stage.sizeToScene();
            }
        };
    }

    private Scene transparentScene(Parent root) {
        return SceneBuilder.create()
                .root(root)
                .fill(Color.TRANSPARENT)
                .build();
    }

    private void showTransparentStage(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }
*/
    private static class AnalogClockwork {

        private static final double HOURS_ON_CLOCK = 12d;
        private static final double SECONDS_ON_CLOCK = 60d;
        private static final double MINUTES_ON_CLOCK = 60d;
        private static final double DEGREES_PER_SECOND = 360d / SECONDS_ON_CLOCK;
        private static final double DEGREES_PER_MINUTE = 360d / MINUTES_ON_CLOCK;
        private static final double DEGREES_PER_HOUR = 360d / HOURS_ON_CLOCK;
        private static final Duration SECONDS_HAND_TICK = Duration.millis(50);
        private static final Duration MINUTE_HAND_TICK = Duration.millis(500);
        private static final Duration HOUR_HAND_TICK = Duration.seconds(10);

        private SimpleDoubleProperty hourHandAngle = new SimpleDoubleProperty(0);
        private SimpleDoubleProperty minuteHandAngle = new SimpleDoubleProperty(0);
        private SimpleDoubleProperty secondsHandAngle = new SimpleDoubleProperty(0);

        public AnalogClockwork() {
            updateHandAngles();
            startTicking();
        }

        public DoubleProperty hourHandAngle() {
            return hourHandAngle;
        }

        public DoubleProperty minuteHandAngle() {
            return minuteHandAngle;
        }

        public DoubleProperty secondsHandAngle() {
            return secondsHandAngle;
        }

        private void updateHandAngles() {
            updateSecondsHandAngle();
            updateMinuteHandAngle();
            updateHourHandAngle();
        }

        private void startTicking() {
            startHandTicking(SECONDS_HAND_TICK, new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    updateSecondsHandAngle();
                }
            });
            startHandTicking(MINUTE_HAND_TICK, new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    updateMinuteHandAngle();
                }
            });
            startHandTicking(HOUR_HAND_TICK, new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    updateHourHandAngle();
                }
            });
        }

        private void startHandTicking(Duration tickDuration, EventHandler<ActionEvent> onTick) {
            TimelineBuilder.create()
                    .cycleCount(Timeline.INDEFINITE)
                    .keyFrames(new KeyFrame(tickDuration, onTick))
                    .build()
                    .play();
        }

        private void updateHourHandAngle() {
            hourHandAngle.set(currentHourWithFractions() * DEGREES_PER_HOUR);
        }

        private void updateMinuteHandAngle() {
            minuteHandAngle.set(currentMinuteWithFractions() * DEGREES_PER_MINUTE);
        }

        private void updateSecondsHandAngle() {
            secondsHandAngle.set(currentSecondsWithFractions() * DEGREES_PER_SECOND);
        }

        private double currentHourWithFractions() {
            double hours = (double) Calendar.getInstance().get(Calendar.HOUR);
            return hours + currentMinuteWithFractions() / MINUTES_ON_CLOCK;
        }

        private double currentMinuteWithFractions() {
            double minutes = (double) Calendar.getInstance().get(Calendar.MINUTE);
            return minutes + currentSecondsWithFractions() / SECONDS_ON_CLOCK;
        }

        private double currentSecondsWithFractions() {
            Calendar calendar = Calendar.getInstance();
            double currentSeconds = calendar.get(Calendar.SECOND);
            double currentMilliseconds = calendar.get(Calendar.MILLISECOND);
            return currentSeconds + currentMilliseconds / 1000d;
        }
    }
}
