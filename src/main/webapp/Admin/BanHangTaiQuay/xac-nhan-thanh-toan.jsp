<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="modal" id="paymentModal" style="display:none;">
    <div class="modal-content" style="max-width: 450px;">
        <span class="close-btn" id="closePaymentModal" onclick="this.parentElement.parentElement.style.display='none'">&times;</span>
        <h2>Xác nhận thanh toán</h2>

        <div class="payment-summary">
            <div class="summary-row">
                <span>Tổng tiền hàng:</span>
                <span id="paymentTotalAmount">0 đ</span>
            </div>
            <div class="summary-row">
                <span>Giảm giá:</span>
                <span id="paymentDiscountAmount">0 đ</span>
            </div>
            <hr>
            <div class="summary-row final-amount">
                <span>Khách cần trả:</span>
                <span id="paymentFinalAmount">0 đ</span>
            </div>
        </div>

        <div class="form-group">
            <label for="customerCashInput">Khách đưa (VNĐ)</label>
            <input type="text" id="customerCashInput" class="filter-input" placeholder="Nhập số tiền khách đưa...">
        </div>

        <div class="summary-row change-due">
            <span>Tiền thối lại:</span>
            <span id="changeDueAmount">0 đ</span>
        </div>

        <div class="payment-methods">
            <p>Chọn phương thức thanh toán:</p>
            <button class="payment-method-btn" data-method="TM" onclick="xacNhanThanhToan(currentInvoiceId, 'TM', parseCurrency(document.getElementById('customerCashInput').value))">Tiền mặt</button>
            <button class="payment-method-btn" data-method="CK" onclick="xacNhanThanhToan(currentInvoiceId, 'CK', parseCurrency(document.getElementById('customerCashInput').value))">Chuyển khoản</button>
            <button class="payment-method-btn" data-method="THE" onclick="xacNhanThanhToan(currentInvoiceId, 'THE', parseCurrency(document.getElementById('customerCashInput').value))">Thẻ</button>
        </div>

        <div class="form-actions">
            <button id="confirmPaymentBtn" class="add-new-btn" disabled>Xác nhận thanh toán</button>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const customerCashInput = document.getElementById('customerCashInput');
        const finalAmountEl = document.getElementById('paymentFinalAmount');
        const changeDueEl = document.getElementById('changeDueAmount');
        const confirmBtn = document.getElementById('confirmPaymentBtn');

        function formatCurrency(value) {
            return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
        }

        function parseCurrency(value) {
            return Number(String(value).replace(/[^0-9]/g, ''));
        }

        customerCashInput.addEventListener('input', function () {
            const finalAmount = parseCurrency(finalAmountEl.textContent);
            let cashGiven = parseCurrency(customerCashInput.value);

            // Format input while typing
            if (!isNaN(cashGiven)) {
               customerCashInput.value = new Intl.NumberFormat('vi-VN').format(cashGiven);
            }

            if (!isNaN(finalAmount) && !isNaN(cashGiven)) {
                if (cashGiven >= finalAmount) {
                    const change = cashGiven - finalAmount;
                    changeDueEl.textContent = formatCurrency(change);
                    confirmBtn.disabled = false;
                } else {
                    changeDueEl.textContent = "Chưa đủ";
                    confirmBtn.disabled = true;
                }
            } else {
                changeDueEl.textContent = "0 đ";
                confirmBtn.disabled = true;
            }
        });
    });
</script>