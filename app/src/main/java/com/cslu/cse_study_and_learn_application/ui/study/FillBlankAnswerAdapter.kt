import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cslu.cse_study_and_learn_application.R
import com.cslu.cse_study_and_learn_application.databinding.ItemFillBlankAnswerBinding
import com.google.android.material.textfield.TextInputLayout

class FillBlankAnswerAdapter(private val context: Context, private val answerCount: Int) :
    RecyclerView.Adapter<FillBlankAnswerAdapter.ViewHolder>() {

    private val userAnswers = MutableList(answerCount) { "" }
    private val isCorrectList = MutableList(answerCount) { false }
    private var isSubmitted = false

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(private val binding: ItemFillBlankAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.textInputLayout.hint = "빈칸 ${position + 1}"
            binding.etAnswer.setText(userAnswers[position])
            binding.etAnswer.isEnabled = !isSubmitted

            if (isSubmitted) {
                updateBackgroundColor(binding.textInputLayout, isCorrectList[position])
            } else {
                resetBackgroundColor(binding.textInputLayout)
            }

            binding.etAnswer.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!isSubmitted) {
                        userAnswers[position] = s.toString()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        private fun updateBackgroundColor(textInputLayout: TextInputLayout, isCorrect: Boolean) {
            val color = if (isCorrect) {
                ContextCompat.getColor(context, R.color.correct_card_background)
            } else {
                ContextCompat.getColor(context, R.color.incorrect_card_background)
            }
            textInputLayout.boxBackgroundColor = color
        }

        private fun resetBackgroundColor(textInputLayout: TextInputLayout) {
            textInputLayout.boxBackgroundColor = ContextCompat.getColor(context, android.R.color.transparent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFillBlankAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = answerCount

    fun getAnswers(): List<String> = userAnswers

    fun submitAnswers(correctAnswers: List<String>) {
        isSubmitted = true
        correctAnswers.forEachIndexed { index, correctAnswer ->
            isCorrectList[index] = userAnswers[index].trim().equals(correctAnswer.trim(), ignoreCase = true)
        }
        notifyDataSetChanged()
    }

    fun resetAnswers() {
        isSubmitted = false
        userAnswers.fill("")
        isCorrectList.fill(false)
        notifyDataSetChanged()
    }
}