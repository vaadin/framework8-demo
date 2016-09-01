/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.demo.registration;

import com.vaadin.data.Result;
import com.vaadin.data.validator.StringLengthValidator;

/**
 * @author Vaadin Ltd
 *
 */
public class PasswordValidator extends StringLengthValidator {

    public PasswordValidator() {
        super("", 6, Integer.MAX_VALUE);
    }

    @Override
    public Result<String> apply(String value) {
        Result<String> result = super.apply(value);
        if (result.isError()) {
            return Result
                    .error("Password should contain at least 6 characters");
        } else if (!hasDigit(value) || !hasLetter(value)) {
            return Result.error("Password must contain a letter and a number");
        }
        return result;
    }

    private boolean hasDigit(String pwd) {
        return pwd.chars().anyMatch(Character::isDigit);
    }

    private boolean hasLetter(String pwd) {
        return pwd.chars().anyMatch(Character::isLetter);
    }
}
