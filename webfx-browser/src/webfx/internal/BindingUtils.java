/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.internal;

import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author attila
 */
public class BindingUtils {
      public static StringExpression convert(final ObservableValue<?> observableValue) {
        if (observableValue == null) {
            throw new NullPointerException("ObservableValue must be specified");
        }
        if (observableValue instanceof StringExpression) {
            return (StringExpression) observableValue;
        } else {
            return new StringBinding() {
                {
                    super.bind(observableValue);
                }

                @Override
                public void dispose() {
                    super.unbind(observableValue);
                }

                @Override
                protected String computeValue() {
                    final Object value = observableValue.getValue();
                    return (value == null)? "" : value.toString();
                }

                @Override
                public ObservableList<ObservableValue<?>> getDependencies() {
                    return FXCollections.<ObservableValue<?>> singletonObservableList(observableValue);
                }
            };
        }
    }
}
