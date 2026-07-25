(() => {
    'use strict';

    const phonePattern = /^0\d{9}$/;
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    const getField = (form, name) => form.elements.namedItem(name);

    const setClientError = (form, fieldName, message) => {
        const errorElement = form.querySelector(`[data-customer-client-error="${fieldName}"]`);
        const field = getField(form, fieldName);

        if (errorElement) {
            errorElement.textContent = message || '';
            errorElement.hidden = !message;
        }

        if (field && typeof field.setAttribute === 'function') {
            if (message) {
                field.setAttribute('aria-invalid', 'true');
            } else {
                field.removeAttribute('aria-invalid');
            }
        }
    };

    const clearClientErrors = (form) => {
        form.querySelectorAll('[data-customer-client-error]').forEach((errorElement) => {
            errorElement.textContent = '';
            errorElement.hidden = true;
        });
        form.querySelectorAll('[aria-invalid="true"]').forEach((field) => {
            field.removeAttribute('aria-invalid');
        });
    };

    const normalizeDate = (date) => {
        const normalized = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        normalized.setHours(0, 0, 0, 0);
        return normalized;
    };

    const validateBirthDate = (value) => {
        if (!value) {
            return '';
        }

        if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) {
            return 'Ngày sinh không hợp lệ.';
        }

        const [year, month, day] = value.split('-').map(Number);
        const date = new Date(year, month - 1, day);
        if (Number.isNaN(date.getTime())
            || date.getFullYear() !== year
            || date.getMonth() !== month - 1
            || date.getDate() !== day) {
            return 'Ngày sinh không hợp lệ.';
        }

        const today = normalizeDate(new Date());
        const oldestAllowed = new Date(today);
        oldestAllowed.setFullYear(today.getFullYear() - 120);

        if (date > today) {
            return 'Ngày sinh không được lớn hơn ngày hiện tại.';
        }
        if (date < oldestAllowed) {
            return 'Tuổi khách hàng không được vượt quá 120.';
        }

        return '';
    };

    const validateCustomerForm = (form) => {
        clearClientErrors(form);
        let firstInvalidField = null;
        let isValid = true;

        const invalidate = (fieldName, message) => {
            setClientError(form, fieldName, message);
            const field = getField(form, fieldName);
            if (!firstInvalidField && field && typeof field.focus === 'function') {
                firstInvalidField = field;
            }
            isValid = false;
        };

        const fullNameField = getField(form, 'hoTen');
        const fullName = fullNameField ? fullNameField.value.trim() : '';
        if (fullNameField) {
            fullNameField.value = fullName;
        }
        if (!fullName) {
            invalidate('hoTen', 'Vui lòng nhập họ tên.');
        } else if (fullName.length < 2) {
            invalidate('hoTen', 'Họ tên phải có ít nhất 2 ký tự.');
        } else if (/^\d+$/.test(fullName)) {
            invalidate('hoTen', 'Họ tên không được chỉ gồm chữ số.');
        } else if (fullName.length > 250) {
            invalidate('hoTen', 'Họ tên không được vượt quá 250 ký tự.');
        }

        const emailField = getField(form, 'email');
        const email = emailField ? emailField.value.trim() : '';
        if (emailField) {
            emailField.value = email;
        }
        if (email && email.length > 150) {
            invalidate('email', 'Email không được vượt quá 150 ký tự.');
        } else if (email && !emailPattern.test(email)) {
            invalidate('email', 'Email không đúng định dạng.');
        }

        const phoneField = getField(form, 'soDienThoai');
        const phone = phoneField ? phoneField.value.trim() : '';
        if (phoneField) {
            phoneField.value = phone;
        }
        if (!phone) {
            invalidate('soDienThoai', 'Vui lòng nhập số điện thoại.');
        } else if (!phonePattern.test(phone)) {
            invalidate('soDienThoai', 'Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.');
        }

        const birthDateField = getField(form, 'ngaySinh');
        const birthDateError = validateBirthDate(birthDateField ? birthDateField.value : '');
        if (birthDateError) {
            invalidate('ngaySinh', birthDateError);
        }

        const genderField = getField(form, 'gioiTinh');
        const gender = genderField ? genderField.value : '';
        if (gender !== '0' && gender !== '1') {
            invalidate('gioiTinh', 'Vui lòng chọn giới tính.');
        }

        if (firstInvalidField) {
            firstInvalidField.focus();
        }
        return isValid;
    };

    const bindCustomerForm = (page) => {
        page.querySelectorAll('[data-customer-form]').forEach((form) => {
            form.addEventListener('submit', (event) => {
                if (!validateCustomerForm(form)) {
                    event.preventDefault();
                }
            });

            ['hoTen', 'email', 'soDienThoai', 'ngaySinh', 'gioiTinh'].forEach((fieldName) => {
                const field = getField(form, fieldName);
                if (!field) {
                    return;
                }
                const eventName = field.tagName === 'SELECT' ? 'change' : 'input';
                field.addEventListener(eventName, () => setClientError(form, fieldName, ''));
            });
        });
    };

    const bindStatusConfirmation = (page) => {
        page.querySelectorAll('[data-customer-status-form]').forEach((form) => {
            form.addEventListener('submit', (event) => {
                const button = form.querySelector('button[data-current-status]');
                const isActive = button && button.dataset.currentStatus === '1';
                const message = isActive
                    ? 'Bạn có chắc chắn muốn ngừng hoạt động khách hàng này không?'
                    : 'Bạn có chắc chắn muốn kích hoạt lại khách hàng này không?';

                if (!window.confirm(message)) {
                    event.preventDefault();
                }
            });
        });
    };

    const bindCustomerFormToggle = (page) => {
        const openButton = document.getElementById('customer-toggle-form');
        const closeButton = document.getElementById('customer-close-form');
        const formPanel = document.getElementById('customer-form-panel');
        const customerForm = page.querySelector('[data-customer-form]');

        if (!openButton || !formPanel) {
            return;
        }

        const focusFirstField = () => {
            const firstInput = formPanel.querySelector(
                "input:not([type='hidden']), select"
            );
            if (firstInput) {
                firstInput.focus();
            }
        };

        const openCustomerForm = () => {
            formPanel.hidden = false;
            formPanel.classList.add('is-open');
            openButton.setAttribute('aria-expanded', 'true');
            focusFirstField();
        };

        const closeCustomerForm = () => {
            if (formPanel.dataset.serverError === 'true') {
                const invalidField = formPanel.querySelector('[aria-invalid="true"]');
                if (invalidField) {
                    invalidField.focus();
                }
                return;
            }

            if (formPanel.dataset.editMode === 'true') {
                const cancelUrl = formPanel.dataset.cancelUrl;
                if (cancelUrl) {
                    window.location.assign(cancelUrl);
                }
                return;
            }

            formPanel.classList.remove('is-open');
            formPanel.hidden = true;
            openButton.setAttribute('aria-expanded', 'false');

            if (customerForm) {
                customerForm.reset();
            }
        };

        if (formPanel.dataset.open === 'true') {
            openCustomerForm();
        }

        openButton.addEventListener('click', openCustomerForm);

        if (closeButton) {
            closeButton.addEventListener('click', closeCustomerForm);
        }
    };

    const bindCustomerSearch = (page) => {
        const form = page.querySelector('[data-customer-search-form]');
        const input = page.querySelector('[data-customer-search-input]');
        const clearButton = page.querySelector('[data-clear-customer-search]');
        const emptyRow = page.querySelector('[data-customer-search-empty]');
        const visibleCountElement = page.querySelector('[data-customer-visible-count]');
        const rows = Array.from(page.querySelectorAll('[data-customer-search-row]'));

        if (!form || !input || rows.length === 0) {
            return;
        }

        const applyFilter = () => {
            const keyword = input.value.trim().toLocaleLowerCase();
            let visibleCount = 0;

            rows.forEach((row) => {
                const matches = !keyword || row.textContent.toLocaleLowerCase().includes(keyword);
                row.hidden = !matches;
                if (matches) {
                    visibleCount += 1;
                }
            });

            if (emptyRow) {
                emptyRow.hidden = !keyword || visibleCount > 0;
            }
            if (clearButton) {
                clearButton.hidden = !keyword;
            }
            if (visibleCountElement) {
                visibleCountElement.textContent = String(visibleCount);
            }
        };

        form.addEventListener('submit', (event) => {
            event.preventDefault();
            applyFilter();
        });
        input.addEventListener('input', applyFilter);

        if (clearButton) {
            clearButton.addEventListener('click', () => {
                input.value = '';
                applyFilter();
                input.focus();
            });
        }
    };

    document.addEventListener('DOMContentLoaded', () => {
        const page = document.querySelector('.customer-page');
        if (!page) {
            return;
        }

        bindCustomerForm(page);
        bindStatusConfirmation(page);
        bindCustomerFormToggle(page);
        bindCustomerSearch(page);
    });
})();
