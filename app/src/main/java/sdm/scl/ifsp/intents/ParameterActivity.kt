package sdm.scl.ifsp.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sdm.scl.ifsp.intents.Extras.PARAMETER_EXTRA
import sdm.scl.ifsp.intents.databinding.ActivityParameterBinding


class ParameterActivity : AppCompatActivity() {
    private val activityParameterBinding: ActivityParameterBinding by lazy {
        ActivityParameterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityParameterBinding.root)
        setSupportActionBar(activityParameterBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        intent.getStringExtra(PARAMETER_EXTRA)?.let {
            activityParameterBinding.parameterEt.setText(it)
        }

        activityParameterBinding.apply {
            returnCloseBt.setOnClickListener {
                val resultIntent = Intent().apply {
                    putExtra(PARAMETER_EXTRA, parameterEt.text.toString())
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }

        }
    }
}